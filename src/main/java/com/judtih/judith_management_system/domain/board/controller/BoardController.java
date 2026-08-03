package com.judtih.judith_management_system.domain.board.controller;

import com.judtih.judith_management_system.domain.board.dto.*;
import com.judtih.judith_management_system.domain.board.enums.Department;
import com.judtih.judith_management_system.domain.board.service.BoardService;
import com.judtih.judith_management_system.global.download.FileDownloadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Board endpoints. Base: /api/board/seasons/{seasonId}/{department}/...
 *
 * SecurityConfig only requires `authenticated` here — every access decision
 * (membership, canPost, ownership, pin rights) lives in BoardService.
 * {department} binds the path segment (e.g. "STAGE_DESIGN") to the enum automatically.
 *
 * Deliberate deviation from the original plan: posts live under an explicit
 * /posts segment instead of a bare /{postId}, so "folders" / "pinned-attachments"
 * can never be swallowed by a numeric path variable.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board/seasons/{seasonId}/{department}")
public class BoardController {

    private final BoardService boardService;
    private final FileDownloadService fileDownloadService;

    // ==================== Folders ====================

    @GetMapping("/folders")
    public ResponseEntity<List<FolderResponse>> getFolders(
            @PathVariable Long seasonId, @PathVariable Department department, Authentication auth) {
        return ResponseEntity.ok(boardService.getFolders(userId(auth), seasonId, department, isFullAccess(auth)));
    }

    @PostMapping("/folders")
    public ResponseEntity<FolderResponse> createFolder(
            @PathVariable Long seasonId, @PathVariable Department department,
            @RequestBody FolderRequest request, Authentication auth) {
        return ResponseEntity.status(201)
                .body(boardService.createFolder(userId(auth), seasonId, department, request, isFullAccess(auth)));
    }

    @PutMapping("/folders/{folderId}")
    public ResponseEntity<Void> renameFolder(
            @PathVariable Long seasonId, @PathVariable Department department,
            @PathVariable Long folderId, @RequestBody FolderRequest request, Authentication auth) {
        boardService.renameFolder(userId(auth), folderId, request.getName(), isFullAccess(auth));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/folders/{folderId}")
    public ResponseEntity<Void> deleteFolder(
            @PathVariable Long seasonId, @PathVariable Department department,
            @PathVariable Long folderId, Authentication auth) {
        boardService.deleteFolder(userId(auth), folderId, isFullAccess(auth));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/folders/{folderId}/read")
    public ResponseEntity<Void> markFolderRead(
            @PathVariable Long seasonId, @PathVariable Department department,
            @PathVariable Long folderId, Authentication auth) {
        boardService.markFolderRead(userId(auth), folderId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/folders/{folderId}/posts")
    public ResponseEntity<List<PostResponse>> getPostsInFolder(
            @PathVariable Long seasonId, @PathVariable Department department,
            @PathVariable Long folderId, Authentication auth) {
        return ResponseEntity.ok(boardService.getPostsInFolder(userId(auth), folderId, isFullAccess(auth)));
    }

    // ==================== Posts ====================

    @GetMapping("/posts")
    public ResponseEntity<List<PostResponse>> getPosts(
            @PathVariable Long seasonId, @PathVariable Department department, Authentication auth) {
        return ResponseEntity.ok(boardService.getPosts(userId(auth), seasonId, department, isFullAccess(auth)));
    }

    @GetMapping("/posts/root")
    public ResponseEntity<List<PostResponse>> getRootPosts(
            @PathVariable Long seasonId, @PathVariable Department department, Authentication auth) {
        return ResponseEntity.ok(boardService.getRootPosts(userId(auth), seasonId, department, isFullAccess(auth)));
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<PostResponse> getPost(
            @PathVariable Long seasonId, @PathVariable Department department,
            @PathVariable Long postId, Authentication auth) {
        return ResponseEntity.ok(boardService.getPost(userId(auth), seasonId, postId, isFullAccess(auth)));
    }

    /** Multipart: "data" = PostRequest JSON, "files" = uploaded FILE/AUDIO attachments (optional). */
    @PostMapping(value = "/posts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostResponse> createPost(
            @PathVariable Long seasonId, @PathVariable Department department,
            @RequestPart("data") PostRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            Authentication auth) {
        return ResponseEntity.status(201)
                .body(boardService.createPost(userId(auth), seasonId, department, request, files, isFullAccess(auth)));
    }

    @PutMapping("/posts/{postId}")
    public ResponseEntity<Void> updatePost(
            @PathVariable Long seasonId, @PathVariable Department department,
            @PathVariable Long postId, @RequestBody PostRequest request, Authentication auth) {
        boardService.updatePost(userId(auth), postId, request.getTitle(), request.getContent(), isFullAccess(auth));
        return ResponseEntity.noContent().build();
    }

    /** targetFolderId omitted/null = move to board root. */
    @PutMapping("/posts/{postId}/move")
    public ResponseEntity<Void> movePost(
            @PathVariable Long seasonId, @PathVariable Department department,
            @PathVariable Long postId,
            @RequestParam(required = false) Long targetFolderId, Authentication auth) {
        boardService.movePost(userId(auth), postId, targetFolderId, isFullAccess(auth));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long seasonId, @PathVariable Department department,
            @PathVariable Long postId, Authentication auth) {
        boardService.deletePost(userId(auth), postId, isFullAccess(auth));
        return ResponseEntity.noContent().build();
    }

    // ==================== Pinned attachments ====================

    @GetMapping("/pinned-attachments")
    public ResponseEntity<List<AttachmentResponse>> getPinnedAttachments(
            @PathVariable Long seasonId, @PathVariable Department department, Authentication auth) {
        return ResponseEntity.ok(boardService.getPinnedAttachments(userId(auth), seasonId, department, isFullAccess(auth)));
    }

    @PostMapping("/attachments/bulk-pin")
    public ResponseEntity<Void> bulkPin(
            @PathVariable Long seasonId, @PathVariable Department department,
            @RequestBody BulkPinRequest request, Authentication auth) {
        boardService.bulkPin(userId(auth), seasonId, request, isFullAccess(auth));
        return ResponseEntity.noContent().build();
    }

    // ==================== Comments ====================

    /** Same multipart pattern as createPost. */
    @PostMapping(value = "/posts/{postId}/comments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long seasonId, @PathVariable Department department,
            @PathVariable Long postId,
            @RequestPart("data") CommentRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            Authentication auth) {
        return ResponseEntity.status(201)
                .body(boardService.addComment(userId(auth), seasonId, postId, request, files, isFullAccess(auth)));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long seasonId, @PathVariable Department department,
            @PathVariable Long commentId, Authentication auth) {
        boardService.deleteComment(userId(auth), commentId, isFullAccess(auth));
        return ResponseEntity.noContent().build();
    }

    // ==================== Download proxy ====================

    /** iOS-safe download — streams S3 bytes through our origin. source = "post" | "comment". */
    @GetMapping("/download/{source}/{attachmentId}")
    public ResponseEntity<byte[]> downloadAttachment(
            @PathVariable Long seasonId, @PathVariable Department department,
            @PathVariable String source, @PathVariable Long attachmentId,
            Authentication auth) throws IOException {
        AttachmentResponse target = boardService.getDownloadTarget(
                userId(auth), seasonId, source, attachmentId, isFullAccess(auth));
        String filename = target.getFileName() != null ? target.getFileName() : "attachment";
        return fileDownloadService.buildDownloadResponse(target.getFileUrl(), filename);
    }

    // ==================== Helpers ====================

    private Long userId(Authentication auth) {
        return (Long) auth.getDetails();
    }

    private boolean isFullAccess(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
