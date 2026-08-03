package com.judtih.judith_management_system.domain.board.service;

import com.judtih.judith_management_system.domain.board.dto.*;
import com.judtih.judith_management_system.domain.board.entity.*;
import com.judtih.judith_management_system.domain.board.enums.ContentType;
import com.judtih.judith_management_system.domain.board.enums.Department;
import com.judtih.judith_management_system.domain.board.exception.BoardAccessDeniedException;
import com.judtih.judith_management_system.domain.board.exception.BoardItemNotFoundException;
import com.judtih.judith_management_system.domain.board.repository.*;
import com.judtih.judith_management_system.domain.dashboard.exception.NotASeasonMemberException;
import com.judtih.judith_management_system.domain.season.Season;
import com.judtih.judith_management_system.domain.season.SeasonRepository;
import com.judtih.judith_management_system.domain.season.Status;
import com.judtih.judith_management_system.domain.season.exception.NoSeasonFoundException;
import com.judtih.judith_management_system.domain.season.exception.SeasonClosedException;
import com.judtih.judith_management_system.domain.user.entity.User;
import com.judtih.judith_management_system.domain.user.entity.UserSeason;
import com.judtih.judith_management_system.domain.user.enums.UserRole;
import com.judtih.judith_management_system.domain.user.repository.UserRepository;
import com.judtih.judith_management_system.domain.user.repository.UserSeasonRepository;
import com.judtih.judith_management_system.global.notification.enums.NotificationType;
import com.judtih.judith_management_system.global.notification.enums.SourceType;
import com.judtih.judith_management_system.global.notification.service.NotificationService;
import com.judtih.judith_management_system.global.storage.StorageFolder;
import com.judtih.judith_management_system.global.storage.dto.StoredFileResponse;
import com.judtih.judith_management_system.global.storage.service.StorageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Department board logic — folders, posts, attachments, comments, pinning.
 *
 * Access model (three layers):
 *  1. SecurityConfig — any authenticated user reaches the endpoints (anyRequest().authenticated()).
 *  2. Read gate — assertReadAccess(): own-season member, current-season member, or hasFullAccess.
 *  3. Write gates — membership in THIS season + Department.canPost() for creating posts;
 *     ownership-or-hasFullAccess for edit/delete; hasFullAccess only for pinning.
 *
 * hasFullAccess comes from the controller (ROLE_ADMIN in JWT — covers both the
 * super admin and full-access season members). The service never decides who is admin.
 *
 * CLOSED season = fully read-only. Writes throw 403.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private final PostRepository postRepository;
    private final PostAttachmentRepository postAttachmentRepository;
    private final CommentRepository commentRepository;
    private final CommentAttachmentRepository commentAttachmentRepository;
    private final BoardFolderRepository boardFolderRepository;
    private final FolderReadStatusRepository folderReadStatusRepository;
    private final UserSeasonRepository userSeasonRepository;
    private final SeasonRepository seasonRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final StorageService storageService;

    // ==================== Folders ====================

    /** Folder list with per-user unread dots and post counts. */
    public List<FolderResponse> getFolders(Long userId, Long seasonId, Department department, boolean hasFullAccess) {
        log.debug("getFolders: userId={}, seasonId={}, department={}", userId, seasonId, department);
        assertReadAccess(userId, seasonId, hasFullAccess);

        List<BoardFolder> folders = boardFolderRepository.findBySeasonIdAndDepartmentOrderByCreatedAtAsc(seasonId, department);
        if (folders.isEmpty()) return List.of();

        // One batch query for this user's read statuses instead of N individual lookups
        List<Long> folderIds = folders.stream().map(BoardFolder::getId).toList();
        Map<Long, LocalDateTime> lastViewedMap = folderReadStatusRepository.findByUserIdAndFolderIdIn(userId, folderIds)
                .stream().collect(Collectors.toMap(rs -> rs.getFolder().getId(), FolderReadStatus::getLastViewedAt));

        return folders.stream().map(f -> {
            List<Post> posts = postRepository.findByFolderIdOrderByCreatedAtDesc(f.getId());
            boolean hasUnread = false;
            if (!posts.isEmpty()) {
                LocalDateTime lastViewed = lastViewedMap.get(f.getId());
                // No read record yet = never opened = unread if anything exists
                hasUnread = lastViewed == null || posts.get(0).getCreatedAt().isAfter(lastViewed);
            }
            return FolderResponse.builder()
                    .id(f.getId())
                    .name(f.getName())
                    .hasUnread(hasUnread)
                    .postCount(posts.size())
                    .createdByUserId(f.getCreatedByUserId())
                    .createdAt(f.getCreatedAt())
                    .build();
        }).toList();
    }

    /** Creating folders requires posting rights — a member who can't post shouldn't reshape the board. */
    @Transactional
    public FolderResponse createFolder(Long userId, Long seasonId, Department department, FolderRequest req, boolean hasFullAccess) {
        log.info("createFolder: userId={}, seasonId={}, department={}, name={}", userId, seasonId, department, req.getName());
        Season season = findSeason(seasonId);
        assertSeasonWritable(season);
        assertCanPost(userId, seasonId, department, hasFullAccess);
        if (req.getName() == null || req.getName().isBlank())
            throw new BoardAccessDeniedException("Folder name is required");

        BoardFolder folder = boardFolderRepository.save(BoardFolder.builder()
                .season(season)
                .department(department)
                .name(req.getName().trim())
                .createdByUserId(userId)
                .build());

        return FolderResponse.builder()
                .id(folder.getId())
                .name(folder.getName())
                .hasUnread(false)
                .postCount(0)
                .createdByUserId(userId)
                .createdAt(folder.getCreatedAt())
                .build();
    }

    @Transactional
    public void renameFolder(Long userId, Long folderId, String name, boolean hasFullAccess) {
        log.info("renameFolder: userId={}, folderId={}", userId, folderId);
        BoardFolder folder = findFolder(folderId);
        assertSeasonWritable(folder.getSeason());
        assertOwnerOrFullAccess(folder.getCreatedByUserId(), userId, hasFullAccess);
        if (name == null || name.isBlank())
            throw new BoardAccessDeniedException("Folder name is required");
        folder.rename(name.trim());
    }

    /** Deleting a folder is non-destructive for posts — they move to root. */
    @Transactional
    public void deleteFolder(Long userId, Long folderId, boolean hasFullAccess) {
        log.info("deleteFolder: userId={}, folderId={}", userId, folderId);
        BoardFolder folder = findFolder(folderId);
        assertSeasonWritable(folder.getSeason());
        assertOwnerOrFullAccess(folder.getCreatedByUserId(), userId, hasFullAccess);

        postRepository.findByFolderIdOrderByCreatedAtDesc(folderId)
                .forEach(post -> post.moveToFolder(null));
        folderReadStatusRepository.deleteByFolderId(folderId);
        boardFolderRepository.delete(folder);
    }

    /** Upsert lastViewedAt = now. Called when the user opens a folder. */
    @Transactional
    public void markFolderRead(Long userId, Long folderId) {
        log.debug("markFolderRead: userId={}, folderId={}", userId, folderId);
        BoardFolder folder = findFolder(folderId);
        folderReadStatusRepository.findByUserIdAndFolderId(userId, folderId)
                .ifPresentOrElse(
                        FolderReadStatus::touch,
                        () -> folderReadStatusRepository.save(FolderReadStatus.builder()
                                .userId(userId).folder(folder).build()));
    }

    // ==================== Posts ====================

    /** Full board feed (all folders + root), newest first. Comments omitted for payload size. */
    public List<PostResponse> getPosts(Long userId, Long seasonId, Department department, boolean hasFullAccess) {
        log.debug("getPosts: userId={}, seasonId={}, department={}", userId, seasonId, department);
        assertReadAccess(userId, seasonId, hasFullAccess);
        List<Post> posts = postRepository.findBySeasonIdAndDepartmentOrderByCreatedAtDesc(seasonId, department);
        return mapPosts(posts);
    }

    /** Posts in one folder. Opening a folder implicitly marks it read. */
    @Transactional
    public List<PostResponse> getPostsInFolder(Long userId, Long folderId, boolean hasFullAccess) {
        log.debug("getPostsInFolder: userId={}, folderId={}", userId, folderId);
        BoardFolder folder = findFolder(folderId);
        assertReadAccess(userId, folder.getSeason().getId(), hasFullAccess);
        markFolderRead(userId, folderId);
        return mapPosts(postRepository.findByFolderIdOrderByCreatedAtDesc(folderId));
    }

    /** Posts at the board root (not in any folder). */
    public List<PostResponse> getRootPosts(Long userId, Long seasonId, Department department, boolean hasFullAccess) {
        log.debug("getRootPosts: userId={}, seasonId={}, department={}", userId, seasonId, department);
        assertReadAccess(userId, seasonId, hasFullAccess);
        return mapPosts(postRepository.findBySeasonIdAndDepartmentAndFolderIsNullOrderByCreatedAtDesc(seasonId, department));
    }

    /** Single post with full comment thread. */
    public PostResponse getPost(Long userId, Long seasonId, Long postId, boolean hasFullAccess) {
        log.debug("getPost: userId={}, seasonId={}, postId={}", userId, seasonId, postId);
        assertReadAccess(userId, seasonId, hasFullAccess);
        Post post = findPostInSeason(postId, seasonId);

        Map<Long, String> names = userNameMapFor(collectUserIds(List.of(post)));
        List<CommentResponse> comments = post.getComments().stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt))
                .map(c -> toCommentResponse(c, names))
                .toList();

        return toPostResponse(post, names, comments);
    }

    /** All pinned attachments (post + comment) of one board — powers the home 공유 파일 row. */
    public List<AttachmentResponse> getPinnedAttachments(Long userId, Long seasonId, Department department, boolean hasFullAccess) {
        log.debug("getPinnedAttachments: userId={}, seasonId={}, department={}", userId, seasonId, department);
        assertReadAccess(userId, seasonId, hasFullAccess);

        List<AttachmentResponse> pinned = new ArrayList<>();
        postAttachmentRepository.findByPost_SeasonIdAndPost_DepartmentAndIsPinnedToDashboardTrue(seasonId, department)
                .forEach(a -> pinned.add(toAttachmentResponse(a)));
        commentAttachmentRepository.findByComment_Post_SeasonIdAndComment_Post_DepartmentAndIsPinnedToDashboardTrue(seasonId, department)
                .forEach(a -> pinned.add(toAttachmentResponse(a)));
        pinned.sort(Comparator.comparing(AttachmentResponse::getCreatedAt).reversed());
        return pinned;
    }

    /**
     * Creates a post with any mix of uploaded files (FILE/AUDIO) and URL attachments.
     * Files upload to S3 under Board/{seasonId}/ — folder placement is DB metadata only.
     * Notifies the department's target roles (+ full access), excluding the author.
     */
    @Transactional
    public PostResponse createPost(Long userId, Long seasonId, Department department,
                                   PostRequest req, List<MultipartFile> files, boolean hasFullAccess) {
        log.info("createPost: userId={}, seasonId={}, department={}, title={}", userId, seasonId, department, req.getTitle());
        Season season = findSeason(seasonId);
        assertSeasonWritable(season);
        assertCanPost(userId, seasonId, department, hasFullAccess);
        if (req.getTitle() == null || req.getTitle().isBlank())
            throw new BoardAccessDeniedException("Title is required");

        BoardFolder folder = null;
        if (req.getFolderId() != null) {
            folder = findFolder(req.getFolderId());
            assertFolderMatchesBoard(folder, seasonId, department);
        }

        Post post = postRepository.save(Post.builder()
                .season(season)
                .department(department)
                .folder(folder)
                .title(req.getTitle().trim())
                .content(req.getContent())
                .createdByUserId(userId)
                .build());

        saveUploadedFiles(files, seasonId,
                (type, stored) -> postAttachmentRepository.save(PostAttachment.builder()
                        .post(post).contentType(type)
                        .fileUrl(stored.getUrl()).fileName(stored.getFileName()).fileSize(stored.getFileSize())
                        .build()));

        saveUrlAttachments(req.getUrlAttachments(),
                linkUrl -> postAttachmentRepository.save(PostAttachment.builder()
                        .post(post).contentType(ContentType.URL).linkUrl(linkUrl)
                        .build()));

        notificationService.sendToSeasonMembers(
                seasonId, department.getTargetRoles(), userId,
                "[" + department.getLabel() + "] 새 게시물: " + post.getTitle(),
                preview(req.getContent()),
                NotificationType.BOARD_POST, SourceType.BOARD, post.getId());

        // Re-read attachments so the response includes what was just saved
        Map<Long, String> names = userNameMapFor(Set.of(userId));
        return PostResponse.builder()
                .id(post.getId())
                .department(department)
                .folderId(folder != null ? folder.getId() : null)
                .folderName(folder != null ? folder.getName() : null)
                .title(post.getTitle())
                .content(post.getContent())
                .attachments(postAttachmentRepository.findByPostId(post.getId()).stream()
                        .map(this::toAttachmentResponse).toList())
                .commentCount(0)
                .createdByUserId(userId)
                .createdByUserName(names.getOrDefault(userId, ""))
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    /** Title/content only — attachments are immutable after creation by design. */
    @Transactional
    public void updatePost(Long userId, Long postId, String title, String content, boolean hasFullAccess) {
        log.info("updatePost: userId={}, postId={}", userId, postId);
        Post post = findPost(postId);
        assertSeasonWritable(post.getSeason());
        assertOwnerOrFullAccess(post.getCreatedByUserId(), userId, hasFullAccess);
        if (title == null || title.isBlank())
            throw new BoardAccessDeniedException("Title is required");
        post.update(title.trim(), content);
    }

    /** Move between folders (or to root with null). S3 keys never change. */
    @Transactional
    public void movePost(Long userId, Long postId, Long targetFolderId, boolean hasFullAccess) {
        log.info("movePost: userId={}, postId={}, targetFolderId={}", userId, postId, targetFolderId);
        Post post = findPost(postId);
        assertSeasonWritable(post.getSeason());
        assertOwnerOrFullAccess(post.getCreatedByUserId(), userId, hasFullAccess);

        if (targetFolderId == null) {
            post.moveToFolder(null);
            return;
        }
        BoardFolder target = findFolder(targetFolderId);
        assertFolderMatchesBoard(target, post.getSeason().getId(), post.getDepartment());
        post.moveToFolder(target);
    }

    /** Cascades to attachments, comments, and comment attachments. S3 objects are left in place. */
    @Transactional
    public void deletePost(Long userId, Long postId, boolean hasFullAccess) {
        log.info("deletePost: userId={}, postId={}", userId, postId);
        Post post = findPost(postId);
        assertSeasonWritable(post.getSeason());
        assertOwnerOrFullAccess(post.getCreatedByUserId(), userId, hasFullAccess);
        postRepository.delete(post);
    }

    // ==================== Pinning ====================

    /** The ONLY pin mechanism. Full-access only — regular members can view pins but never change them. */
    @Transactional
    public void bulkPin(Long userId, Long seasonId, BulkPinRequest req, boolean hasFullAccess) {
        log.info("bulkPin: userId={}, seasonId={}, pinned={}", userId, seasonId, req.isPinned());
        if (!hasFullAccess)
            throw new BoardAccessDeniedException("Only full-access members can pin attachments");

        if (req.getPostAttachmentIds() != null) {
            for (Long id : req.getPostAttachmentIds()) {
                PostAttachment a = postAttachmentRepository.findById(id)
                        .orElseThrow(() -> new BoardItemNotFoundException("Post attachment not found: " + id));
                // Guard: attachment must belong to the season in the URL — prevents cross-season IDOR
                if (!a.getPost().getSeason().getId().equals(seasonId))
                    throw new BoardAccessDeniedException("Attachment does not belong to this season");
                if (req.isPinned()) a.pin(); else a.unpin();
            }
        }
        if (req.getCommentAttachmentIds() != null) {
            for (Long id : req.getCommentAttachmentIds()) {
                CommentAttachment a = commentAttachmentRepository.findById(id)
                        .orElseThrow(() -> new BoardItemNotFoundException("Comment attachment not found: " + id));
                if (!a.getComment().getPost().getSeason().getId().equals(seasonId))
                    throw new BoardAccessDeniedException("Attachment does not belong to this season");
                if (req.isPinned()) a.pin(); else a.unpin();
            }
        }
    }

    // ==================== Comments ====================

    /** Any season member can comment on any post they can read — commenting is not gated by canPost. */
    @Transactional
    public CommentResponse addComment(Long userId, Long seasonId, Long postId,
                                      CommentRequest req, List<MultipartFile> files, boolean hasFullAccess) {
        log.info("addComment: userId={}, seasonId={}, postId={}", userId, seasonId, postId);
        Post post = findPostInSeason(postId, seasonId);
        assertSeasonWritable(post.getSeason());
        assertMembership(userId, seasonId, hasFullAccess);

        boolean hasContent = req.getContent() != null && !req.getContent().isBlank();
        boolean hasFiles = files != null && !files.isEmpty();
        boolean hasUrls = req.getUrlAttachments() != null && !req.getUrlAttachments().isEmpty();
        if (!hasContent && !hasFiles && !hasUrls)
            throw new BoardAccessDeniedException("Comment needs content or at least one attachment");

        Comment comment = commentRepository.save(Comment.builder()
                .post(post)
                .content(hasContent ? req.getContent() : null)
                .createdByUserId(userId)
                .build());

        saveUploadedFiles(files, seasonId,
                (type, stored) -> commentAttachmentRepository.save(CommentAttachment.builder()
                        .comment(comment).contentType(type)
                        .fileUrl(stored.getUrl()).fileName(stored.getFileName()).fileSize(stored.getFileSize())
                        .build()));

        saveUrlAttachments(req.getUrlAttachments(),
                linkUrl -> commentAttachmentRepository.save(CommentAttachment.builder()
                        .comment(comment).contentType(ContentType.URL).linkUrl(linkUrl)
                        .build()));

        // Notify the post author — unless they commented on their own post
        if (!post.getCreatedByUserId().equals(userId)) {
            userRepository.findById(post.getCreatedByUserId()).ifPresent(author ->
                    notificationService.sendToUser(author,
                            "내 게시물에 댓글이 달렸습니다: " + post.getTitle(),
                            preview(req.getContent()),
                            NotificationType.BOARD_COMMENT, SourceType.BOARD, post.getId()));
        }

        Map<Long, String> names = userNameMapFor(Set.of(userId));
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .attachments(commentAttachmentRepository.findByCommentId(comment.getId()).stream()
                        .map(this::toAttachmentResponse).toList())
                .createdByUserId(userId)
                .createdByUserName(names.getOrDefault(userId, ""))
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    @Transactional
    public void deleteComment(Long userId, Long commentId, boolean hasFullAccess) {
        log.info("deleteComment: userId={}, commentId={}", userId, commentId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BoardItemNotFoundException("Comment not found: " + commentId));
        assertSeasonWritable(comment.getPost().getSeason());
        assertOwnerOrFullAccess(comment.getCreatedByUserId(), userId, hasFullAccess);
        commentRepository.delete(comment);
    }

    // ==================== Download ====================

    /** Resolves an attachment's file URL + name for the controller's download proxy.
     *  source: "post" or "comment" — attachments live in two tables. */
    public AttachmentResponse getDownloadTarget(Long userId, Long seasonId, String source, Long attachmentId, boolean hasFullAccess) {
        log.debug("getDownloadTarget: userId={}, source={}, attachmentId={}", userId, source, attachmentId);
        assertReadAccess(userId, seasonId, hasFullAccess);

        if ("post".equalsIgnoreCase(source)) {
            PostAttachment a = postAttachmentRepository.findById(attachmentId)
                    .orElseThrow(() -> new BoardItemNotFoundException("Attachment not found: " + attachmentId));
            if (!a.getPost().getSeason().getId().equals(seasonId))
                throw new BoardAccessDeniedException("Attachment does not belong to this season");
            if (a.getFileUrl() == null)
                throw new BoardItemNotFoundException("This attachment has no downloadable file (URL type)");
            return toAttachmentResponse(a);
        }
        if ("comment".equalsIgnoreCase(source)) {
            CommentAttachment a = commentAttachmentRepository.findById(attachmentId)
                    .orElseThrow(() -> new BoardItemNotFoundException("Attachment not found: " + attachmentId));
            if (!a.getComment().getPost().getSeason().getId().equals(seasonId))
                throw new BoardAccessDeniedException("Attachment does not belong to this season");
            if (a.getFileUrl() == null)
                throw new BoardItemNotFoundException("This attachment has no downloadable file (URL type)");
            return toAttachmentResponse(a);
        }
        throw new BoardItemNotFoundException("Unknown attachment source: " + source);
    }

    // ==================== Shared helpers ====================

    /** Uploads each multipart file to S3 and hands the result to the given saver.
     *  audio/* MIME types become AUDIO attachments (frontend renders a player), everything else FILE. */
    private void saveUploadedFiles(List<MultipartFile> files, Long seasonId,
                                   java.util.function.BiConsumer<ContentType, StoredFileResponse> saver) {
        if (files == null) return;
        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;
            ContentType type = file.getContentType() != null && file.getContentType().startsWith("audio/")
                    ? ContentType.AUDIO : ContentType.FILE;
            StoredFileResponse stored = storageService.uploadFile(file, StorageFolder.BOARD, seasonId);
            saver.accept(type, stored);
        }
    }

    private void saveUrlAttachments(List<AttachmentRequest> urls, java.util.function.Consumer<String> saver) {
        if (urls == null) return;
        for (AttachmentRequest url : urls) {
            if (url.getLinkUrl() == null || url.getLinkUrl().isBlank()) continue;
            saver.accept(url.getLinkUrl().trim());
        }
    }

    /** Batch-maps posts to responses with a single user-name lookup for all authors. */
    private List<PostResponse> mapPosts(List<Post> posts) {
        if (posts.isEmpty()) return List.of();
        Map<Long, String> names = userNameMapFor(collectUserIds(posts));
        return posts.stream().map(p -> toPostResponse(p, names, null)).toList();
    }

    private Set<Long> collectUserIds(List<Post> posts) {
        Set<Long> ids = new HashSet<>();
        for (Post p : posts) {
            ids.add(p.getCreatedByUserId());
            p.getComments().forEach(c -> ids.add(c.getCreatedByUserId()));
        }
        return ids;
    }

    private Map<Long, String> userNameMapFor(Set<Long> userIds) {
        return userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, User::getName));
    }

    private PostResponse toPostResponse(Post p, Map<Long, String> names, List<CommentResponse> comments) {
        return PostResponse.builder()
                .id(p.getId())
                .department(p.getDepartment())
                .folderId(p.getFolder() != null ? p.getFolder().getId() : null)
                .folderName(p.getFolder() != null ? p.getFolder().getName() : null)
                .title(p.getTitle())
                .content(p.getContent())
                .attachments(p.getAttachments().stream().map(this::toAttachmentResponse).toList())
                .commentCount(p.getComments().size())
                .createdByUserId(p.getCreatedByUserId())
                .createdByUserName(names.getOrDefault(p.getCreatedByUserId(), ""))
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .comments(comments)
                .build();
    }

    private CommentResponse toCommentResponse(Comment c, Map<Long, String> names) {
        return CommentResponse.builder()
                .id(c.getId())
                .content(c.getContent())
                .attachments(c.getAttachments().stream().map(this::toAttachmentResponse).toList())
                .createdByUserId(c.getCreatedByUserId())
                .createdByUserName(names.getOrDefault(c.getCreatedByUserId(), ""))
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }

    private AttachmentResponse toAttachmentResponse(PostAttachment a) {
        return AttachmentResponse.builder()
                .source("POST")
                .id(a.getId())
                .postId(a.getPost().getId())
                .contentType(a.getContentType())
                .fileUrl(a.getFileUrl())
                .fileName(a.getFileName())
                .fileSize(a.getFileSize())
                .linkUrl(a.getLinkUrl())
                .isPinnedToDashboard(a.isPinnedToDashboard())
                .createdAt(a.getCreatedAt())
                .build();
    }

    private AttachmentResponse toAttachmentResponse(CommentAttachment a) {
        return AttachmentResponse.builder()
                .source("COMMENT")
                .id(a.getId())
                .postId(a.getComment().getPost().getId())
                .commentId(a.getComment().getId())
                .contentType(a.getContentType())
                .fileUrl(a.getFileUrl())
                .fileName(a.getFileName())
                .fileSize(a.getFileSize())
                .linkUrl(a.getLinkUrl())
                .isPinnedToDashboard(a.isPinnedToDashboard())
                .createdAt(a.getCreatedAt())
                .build();
    }

    private String preview(String content) {
        if (content == null || content.isBlank()) return "";
        return content.length() > 100 ? content.substring(0, 100) + "..." : content;
    }

    // ==================== Guards ====================

    private Season findSeason(Long seasonId) {
        return seasonRepository.findById(seasonId)
                .orElseThrow(() -> new NoSeasonFoundException("Season not found: " + seasonId, 404, "Not Found"));
    }

    private BoardFolder findFolder(Long folderId) {
        return boardFolderRepository.findById(folderId)
                .orElseThrow(() -> new BoardItemNotFoundException("Folder not found: " + folderId));
    }

    private Post findPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new BoardItemNotFoundException("Post not found: " + postId));
    }

    /** 404 (not 403) when the post exists but under a different season — don't leak existence. */
    private Post findPostInSeason(Long postId, Long seasonId) {
        Post post = findPost(postId);
        if (!post.getSeason().getId().equals(seasonId))
            throw new BoardItemNotFoundException("Post not found: " + postId);
        return post;
    }

    /** A folder can only hold posts of its own season+department — prevents cross-board filing. */
    private void assertFolderMatchesBoard(BoardFolder folder, Long seasonId, Department department) {
        if (!folder.getSeason().getId().equals(seasonId) || folder.getDepartment() != department)
            throw new BoardAccessDeniedException("Folder belongs to a different board");
    }

    /** CLOSED = read-only for everyone, full access included. PREPARING and ACTIVE are writable. */
    private void assertSeasonWritable(Season season) {
        if (season.getStatus() == Status.CLOSED)
            throw new SeasonClosedException("This season is closed — board is read-only", 403, "Forbidden");
    }

    /** Write-side membership: must belong to THIS season (or be full access / super admin). */
    private void assertMembership(Long userId, Long seasonId, boolean hasFullAccess) {
        if (hasFullAccess) return;
        if (!userSeasonRepository.existsByUserIdAndSeasonId(userId, seasonId))
            throw new NotASeasonMemberException("Not a member of this season");
    }

    /** Posting gate: membership + Department.canPost on the member's roles.
     *  hasFullAccess bypass covers the super admin who has no UserSeason rows. */
    private void assertCanPost(Long userId, Long seasonId, Department department, boolean hasFullAccess) {
        if (hasFullAccess) return;
        Set<UserRole> roles = userSeasonRepository.findByUserIdAndSeasonId(userId, seasonId)
                .map(UserSeason::getUserRoles)
                .orElseThrow(() -> new NotASeasonMemberException("Not a member of this season"));
        if (!department.canPost(roles))
            throw new BoardAccessDeniedException("Your roles cannot post to " + department.getLabel());
    }

    private void assertOwnerOrFullAccess(Long ownerId, Long userId, boolean hasFullAccess) {
        if (hasFullAccess) return;
        if (!ownerId.equals(userId))
            throw new BoardAccessDeniedException("Only the author or a full-access member can do this");
    }

    /** Read gate — mirrors AnnouncementService: own season, any current (ACTIVE/PREPARING) season member, or full access. */
    private static final List<Status> CURRENT_SEASON_STATUSES = List.of(Status.ACTIVE, Status.PREPARING);

    private void assertReadAccess(Long userId, Long seasonId, boolean hasFullAccess) {
        if (hasFullAccess) return;
        if (userSeasonRepository.existsByUserIdAndSeasonId(userId, seasonId)) return;
        if (userSeasonRepository.existsByUserIdAndSeason_StatusIn(userId, CURRENT_SEASON_STATUSES)) return;
        throw new NotASeasonMemberException("Not a member of this season");
    }
}
