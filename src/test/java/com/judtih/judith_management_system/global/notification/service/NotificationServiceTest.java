package com.judtih.judith_management_system.global.notification.service;

import com.judtih.judith_management_system.domain.season.Season;
import com.judtih.judith_management_system.domain.season.SeasonRepository;
import com.judtih.judith_management_system.domain.season.Status;
import com.judtih.judith_management_system.domain.user.entity.User;
import com.judtih.judith_management_system.domain.user.entity.UserSeason;
import com.judtih.judith_management_system.domain.user.enums.UserRole;
import com.judtih.judith_management_system.domain.user.enums.UserStatus;
import com.judtih.judith_management_system.domain.user.repository.UserSeasonRepository;
import com.judtih.judith_management_system.global.notification.dto.NotificationResponse;
import com.judtih.judith_management_system.global.notification.dto.UserNotificationRequest;
import com.judtih.judith_management_system.global.notification.entity.Notification;
import com.judtih.judith_management_system.global.notification.entity.UserNotification;
import com.judtih.judith_management_system.global.notification.enums.NotificationType;
import com.judtih.judith_management_system.global.notification.enums.SourceType;
import com.judtih.judith_management_system.global.notification.exception.NoNotificationFoundException;
import com.judtih.judith_management_system.global.notification.repository.NotificationRepository;
import com.judtih.judith_management_system.global.notification.repository.UserNotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserNotificationRepository userNotificationRepository;

    @Mock
    private UserSeasonRepository userSeasonRepository;

    @Mock
    private SeasonRepository seasonRepository;

    @InjectMocks
    private NotificationService notificationService;

    private User activeUser(String name, String studentNumber) {
        return User.builder()
                .name(name)
                .studentNumber(studentNumber)
                .phoneNumber("01000000000")
                .password("encoded")
                .build();
        // status defaults to ACTIVE in the entity
    }

    private UserNotificationRequest broadcastRequest() {
        return UserNotificationRequest.builder()
                .title("공지사항")
                .content("내용입니다")
                .notificationType(NotificationType.ANNOUNCEMENT)
                .sourceType(SourceType.LMS)
                .sourceId(1L)
                .targetRoles(null)
                .build();
    }

    @Test
    @SuppressWarnings("unchecked")
    void createNotification_shouldDeliverToAllMembers_whenTargetRolesEmpty() {
        Season season = new Season("2026 정기공연");
        User user1 = activeUser("홍길동", "20240001");
        User user2 = activeUser("김철수", "20240002");

        UserSeason us1 = UserSeason.builder().user(user1).season(season).userRoles(Set.of(UserRole.ACTOR)).build();
        UserSeason us2 = UserSeason.builder().user(user2).season(season).userRoles(Set.of(UserRole.LEADER)).build();

        when(seasonRepository.findByStatus(Status.ACTIVE)).thenReturn(Optional.of(season));
        when(userSeasonRepository.findBySeasonId(any())).thenReturn(List.of(us1, us2));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));

        NotificationResponse result = notificationService.createNotification(broadcastRequest());

        ArgumentCaptor<List<UserNotification>> captor = ArgumentCaptor.forClass(List.class);
        verify(userNotificationRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).hasSize(2);
        assertThat(result.getRecipientCount()).isEqualTo(2);
    }

    @Test
    @SuppressWarnings("unchecked")
    void createNotification_shouldFilterByRole_whenTargetRolesSet() {
        Season season = new Season("2026 정기공연");
        User leaderUser = activeUser("홍길동", "20240001");
        User actorUser = activeUser("김철수", "20240002");

        UserSeason leaderUs = UserSeason.builder().user(leaderUser).season(season).userRoles(Set.of(UserRole.LEADER)).build();
        UserSeason actorUs = UserSeason.builder().user(actorUser).season(season).userRoles(Set.of(UserRole.ACTOR)).build();

        UserNotificationRequest roleFilteredRequest = UserNotificationRequest.builder()
                .title("리더 공지")
                .content("리더만 보는 내용")
                .notificationType(NotificationType.ANNOUNCEMENT)
                .sourceType(SourceType.LMS)
                .sourceId(1L)
                .targetRoles(List.of(UserRole.LEADER))
                .build();

        when(seasonRepository.findByStatus(Status.ACTIVE)).thenReturn(Optional.of(season));
        when(userSeasonRepository.findBySeasonId(any())).thenReturn(List.of(leaderUs, actorUs));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));

        NotificationResponse result = notificationService.createNotification(roleFilteredRequest);

        ArgumentCaptor<List<UserNotification>> captor = ArgumentCaptor.forClass(List.class);
        verify(userNotificationRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).hasSize(1);
        assertThat(result.getRecipientCount()).isEqualTo(1);
    }

    @Test
    @SuppressWarnings("unchecked")
    void createNotification_shouldDeliverNobody_whenNoActiveSeason() {
        when(seasonRepository.findByStatus(Status.ACTIVE)).thenReturn(Optional.empty());
        when(notificationRepository.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));

        NotificationResponse result = notificationService.createNotification(broadcastRequest());

        ArgumentCaptor<List<UserNotification>> captor = ArgumentCaptor.forClass(List.class);
        verify(userNotificationRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).isEmpty();
        assertThat(result.getRecipientCount()).isEqualTo(0);
    }

    @Test
    void createNotificationForOneUser_shouldSaveOneNotification() {
        User user = activeUser("홍길동", "20240001");
        Notification notification = Notification.builder()
                .title("비밀번호 변경 알림")
                .content("비밀번호를 변경해 주세요")
                .notificationType(NotificationType.ANNOUNCEMENT)
                .sourceType(SourceType.LMS)
                .sourceId(1L)
                .build();

        notificationService.createNotificationForOneUser(user, notification);

        verify(notificationRepository).save(notification);
        verify(userNotificationRepository).save(any(UserNotification.class));
    }

    @Test
    void markAsRead_shouldThrow_whenNotFound() {
        when(userNotificationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.markAsRead(99L))
                .isInstanceOf(NoNotificationFoundException.class);
    }

    @Test
    void markAllAsRead_shouldMarkAllUnread() {
        User user = activeUser("홍길동", "20240001");
        Notification notification = Notification.builder()
                .title("공지사항")
                .content("내용입니다")
                .notificationType(NotificationType.ANNOUNCEMENT)
                .sourceType(SourceType.LMS)
                .sourceId(1L)
                .build();

        UserNotification un1 = new UserNotification(user, notification);
        UserNotification un2 = new UserNotification(user, notification);

        assertThat(un1.isRead()).isFalse();
        assertThat(un2.isRead()).isFalse();

        when(userNotificationRepository.findByUserIdAndIsReadFalse(1L)).thenReturn(List.of(un1, un2));

        notificationService.markAllAsRead(1L);

        assertThat(un1.isRead()).isTrue();
        assertThat(un2.isRead()).isTrue();
    }
}
