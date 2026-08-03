package com.judtih.judith_management_system.domain.board.service;

import com.judtih.judith_management_system.domain.board.dto.BulkPinRequest;
import com.judtih.judith_management_system.domain.board.dto.PostRequest;
import com.judtih.judith_management_system.domain.board.entity.BoardFolder;
import com.judtih.judith_management_system.domain.board.entity.Post;
import com.judtih.judith_management_system.domain.board.entity.PostAttachment;
import com.judtih.judith_management_system.domain.board.enums.Department;
import com.judtih.judith_management_system.domain.board.exception.BoardAccessDeniedException;
import com.judtih.judith_management_system.domain.board.exception.BoardItemNotFoundException;
import com.judtih.judith_management_system.domain.board.repository.*;
import com.judtih.judith_management_system.domain.dashboard.exception.NotASeasonMemberException;
import com.judtih.judith_management_system.domain.season.Season;
import com.judtih.judith_management_system.domain.season.SeasonRepository;
import com.judtih.judith_management_system.domain.season.exception.SeasonClosedException;
import com.judtih.judith_management_system.domain.user.entity.User;
import com.judtih.judith_management_system.domain.user.entity.UserSeason;
import com.judtih.judith_management_system.domain.user.enums.UserRole;
import com.judtih.judith_management_system.domain.user.repository.UserRepository;
import com.judtih.judith_management_system.domain.user.repository.UserSeasonRepository;
import com.judtih.judith_management_system.global.notification.service.NotificationService;
import com.judtih.judith_management_system.global.storage.service.StorageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BoardServiceTest {

    @Mock private PostRepository postRepository;
    @Mock private PostAttachmentRepository postAttachmentRepository;
    @Mock private CommentRepository commentRepository;
    @Mock private CommentAttachmentRepository commentAttachmentRepository;
    @Mock private BoardFolderRepository boardFolderRepository;
    @Mock private FolderReadStatusRepository folderReadStatusRepository;
    @Mock private UserSeasonRepository userSeasonRepository;
    @Mock private SeasonRepository seasonRepository;
    @Mock private UserRepository userRepository;
    @Mock private NotificationService notificationService;
    @Mock private StorageService storageService;

    @InjectMocks
    private BoardService boardService;

    // ---------- fixtures ----------

    private Season activeSeason() {
        Season season = new Season("2026 여름");
        season.activateSeason();
        return season;
    }

    private Season closedSeason() {
        Season season = new Season("2025 겨울");
        season.activateSeason();
        season.closeSeason();
        return season;
    }

    private User user(String name) {
        return User.builder().name(name).studentNumber("20230001")
                .phoneNumber("01000000000").password("pw").isAdmin(false).build();
    }

    private void givenMemberWithRoles(Long userId, Long seasonId, Set<UserRole> roles) {
        UserSeason us = UserSeason.builder().user(user("member")).userRoles(roles).build();
        when(userSeasonRepository.findByUserIdAndSeasonId(userId, seasonId)).thenReturn(Optional.of(us));
    }

    // ---------- createPost ----------

    @Test
    void createPost_shouldSucceed_whenMemberHasDepartmentRole() {
        when(seasonRepository.findById(1L)).thenReturn(Optional.of(activeSeason()));
        givenMemberWithRoles(10L, 1L, Set.of(UserRole.STAGE_DESIGN));
        when(postRepository.save(any(Post.class))).thenAnswer(inv -> inv.getArgument(0));
        when(postAttachmentRepository.findByPostId(any())).thenReturn(List.of());
        when(userRepository.findAllById(any())).thenReturn(List.of());

        PostRequest req = new PostRequest("무대 도면 v1", "내용", null, null);
        var response = boardService.createPost(10L, 1L, Department.STAGE_DESIGN, req, null, false);

        assertThat(response.getTitle()).isEqualTo("무대 도면 v1");
        verify(postRepository).save(any(Post.class));
        // Author (10L) is excluded from the fan-out
        verify(notificationService).sendToSeasonMembers(eq(1L), eq(Department.STAGE_DESIGN.getTargetRoles()),
                eq(10L), any(), any(), any(), any(), any());
    }

    @Test
    void createPost_shouldThrow403_whenRoleCannotPost() {
        when(seasonRepository.findById(1L)).thenReturn(Optional.of(activeSeason()));
        givenMemberWithRoles(10L, 1L, Set.of(UserRole.ACTOR));

        PostRequest req = new PostRequest("제목", null, null, null);
        assertThatThrownBy(() -> boardService.createPost(10L, 1L, Department.STAGE_DESIGN, req, null, false))
                .isInstanceOf(BoardAccessDeniedException.class);
        verify(postRepository, never()).save(any());
    }

    @Test
    void createPost_shouldThrow_whenSeasonClosed() {
        when(seasonRepository.findById(1L)).thenReturn(Optional.of(closedSeason()));

        PostRequest req = new PostRequest("제목", null, null, null);
        assertThatThrownBy(() -> boardService.createPost(10L, 1L, Department.STAGE_DESIGN, req, null, false))
                .isInstanceOf(SeasonClosedException.class);
    }

    @Test
    void createPost_shouldBypassRoleCheck_whenFullAccess() {
        when(seasonRepository.findById(1L)).thenReturn(Optional.of(activeSeason()));
        when(postRepository.save(any(Post.class))).thenAnswer(inv -> inv.getArgument(0));
        when(postAttachmentRepository.findByPostId(any())).thenReturn(List.of());
        when(userRepository.findAllById(any())).thenReturn(List.of());

        PostRequest req = new PostRequest("공지", null, null, null);
        // Super admin: no UserSeason rows, hasFullAccess=true
        var response = boardService.createPost(99L, 1L, Department.STAGE_DESIGN, req, null, true);

        assertThat(response).isNotNull();
        verify(userSeasonRepository, never()).findByUserIdAndSeasonId(any(), any());
    }

    @Test
    void createPost_shouldThrow_whenNotSeasonMember() {
        when(seasonRepository.findById(1L)).thenReturn(Optional.of(activeSeason()));
        when(userSeasonRepository.findByUserIdAndSeasonId(10L, 1L)).thenReturn(Optional.empty());

        PostRequest req = new PostRequest("제목", null, null, null);
        assertThatThrownBy(() -> boardService.createPost(10L, 1L, Department.PROP_DESIGN, req, null, false))
                .isInstanceOf(NotASeasonMemberException.class);
    }

    @Test
    void createPost_shouldRejectFolderFromAnotherBoard() {
        Season season = activeSeason();
        when(seasonRepository.findById(1L)).thenReturn(Optional.of(season));
        givenMemberWithRoles(10L, 1L, Set.of(UserRole.STAGE_DESIGN));

        // Folder belongs to SOUND_DESIGN — filing a STAGE_DESIGN post into it must fail
        BoardFolder wrongFolder = mock(BoardFolder.class, RETURNS_DEEP_STUBS);
        when(wrongFolder.getSeason().getId()).thenReturn(1L);
        when(wrongFolder.getDepartment()).thenReturn(Department.SOUND_DESIGN);
        when(boardFolderRepository.findById(5L)).thenReturn(Optional.of(wrongFolder));

        PostRequest req = new PostRequest("제목", null, 5L, null);
        assertThatThrownBy(() -> boardService.createPost(10L, 1L, Department.STAGE_DESIGN, req, null, false))
                .isInstanceOf(BoardAccessDeniedException.class);
    }

    // ---------- getPost ----------

    @Test
    void getPost_shouldThrow404_whenPostBelongsToDifferentSeason() {
        Post post = mock(Post.class, RETURNS_DEEP_STUBS);
        when(post.getSeason().getId()).thenReturn(2L);   // exists, but under season 2
        when(postRepository.findById(7L)).thenReturn(Optional.of(post));

        assertThatThrownBy(() -> boardService.getPost(99L, 1L, 7L, true))
                .isInstanceOf(BoardItemNotFoundException.class);
    }

    // ---------- bulkPin ----------

    @Test
    void bulkPin_shouldThrow403_forNonFullAccess() {
        BulkPinRequest req = new BulkPinRequest(List.of(1L), null, true);
        assertThatThrownBy(() -> boardService.bulkPin(10L, 1L, req, false))
                .isInstanceOf(BoardAccessDeniedException.class);
    }

    @Test
    void bulkPin_shouldPinAttachment_whenFullAccess() {
        PostAttachment attachment = mock(PostAttachment.class, RETURNS_DEEP_STUBS);
        when(attachment.getPost().getSeason().getId()).thenReturn(1L);
        when(postAttachmentRepository.findById(3L)).thenReturn(Optional.of(attachment));

        boardService.bulkPin(99L, 1L, new BulkPinRequest(List.of(3L), null, true), true);

        verify(attachment).pin();
    }

    @Test
    void bulkPin_shouldRejectAttachmentFromAnotherSeason() {
        PostAttachment attachment = mock(PostAttachment.class, RETURNS_DEEP_STUBS);
        when(attachment.getPost().getSeason().getId()).thenReturn(2L);  // different season
        when(postAttachmentRepository.findById(3L)).thenReturn(Optional.of(attachment));

        assertThatThrownBy(() -> boardService.bulkPin(99L, 1L, new BulkPinRequest(List.of(3L), null, true), true))
                .isInstanceOf(BoardAccessDeniedException.class);
        verify(attachment, never()).pin();
    }

    // ---------- delete guards ----------

    @Test
    void deletePost_shouldThrow403_whenNotOwnerAndNotFullAccess() {
        Post post = mock(Post.class, RETURNS_DEEP_STUBS);
        when(post.getSeason().getStatus()).thenReturn(com.judtih.judith_management_system.domain.season.Status.ACTIVE);
        when(post.getCreatedByUserId()).thenReturn(10L);
        when(postRepository.findById(7L)).thenReturn(Optional.of(post));

        assertThatThrownBy(() -> boardService.deletePost(20L, 7L, false))
                .isInstanceOf(BoardAccessDeniedException.class);
        verify(postRepository, never()).delete(any(Post.class));
    }

    @Test
    void deletePost_shouldSucceed_forOwner() {
        Post post = mock(Post.class, RETURNS_DEEP_STUBS);
        when(post.getSeason().getStatus()).thenReturn(com.judtih.judith_management_system.domain.season.Status.ACTIVE);
        when(post.getCreatedByUserId()).thenReturn(10L);
        when(postRepository.findById(7L)).thenReturn(Optional.of(post));

        boardService.deletePost(10L, 7L, false);

        verify(postRepository).delete(post);
    }

    // ---------- folders ----------

    @Test
    void deleteFolder_shouldMovePostsToRoot_beforeDeleting() {
        BoardFolder folder = mock(BoardFolder.class, RETURNS_DEEP_STUBS);
        when(folder.getSeason().getStatus()).thenReturn(com.judtih.judith_management_system.domain.season.Status.ACTIVE);
        when(folder.getCreatedByUserId()).thenReturn(10L);
        when(boardFolderRepository.findById(5L)).thenReturn(Optional.of(folder));

        Post postInFolder = mock(Post.class);
        when(postRepository.findByFolderIdOrderByCreatedAtDesc(5L)).thenReturn(List.of(postInFolder));

        boardService.deleteFolder(10L, 5L, false);

        verify(postInFolder).moveToFolder(null);            // posts survive, moved to root
        verify(folderReadStatusRepository).deleteByFolderId(5L);
        verify(boardFolderRepository).delete(folder);
    }

    @Test
    void markFolderRead_shouldCreateStatus_whenNoneExists() {
        BoardFolder folder = mock(BoardFolder.class);
        when(boardFolderRepository.findById(5L)).thenReturn(Optional.of(folder));
        when(folderReadStatusRepository.findByUserIdAndFolderId(10L, 5L)).thenReturn(Optional.empty());

        boardService.markFolderRead(10L, 5L);

        verify(folderReadStatusRepository).save(any());
    }
}
