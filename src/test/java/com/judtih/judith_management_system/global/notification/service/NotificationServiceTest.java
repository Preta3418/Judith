package com.judtih.judith_management_system.global.notification.service;

import com.judtih.judith_management_system.domain.user.entity.User;
import com.judtih.judith_management_system.domain.user.entity.UserSeason;
import com.judtih.judith_management_system.domain.user.enums.UserRole;
import com.judtih.judith_management_system.domain.user.repository.UserSeasonRepository;
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

    @InjectMocks
    private NotificationService notificationService;

    private User activeUser(String name, String studentNumber) {
        return User.builder()
                .name(name)
                .studentNumber(studentNumber)
                .phoneNumber("01000000000")
                .password("encoded")
                .build();
    }

    @Test
    void sendToUser_shouldSaveNotificationAndOneUserNotification() {
        User user = activeUser("홍길동", "20240001");
        when(notificationRepository.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));

        notificationService.sendToUser(user, "제목", "내용", NotificationType.PASSWORD_NOT_CHANGED, SourceType.AUTH, null);

        verify(notificationRepository).save(any(Notification.class));
        verify(userNotificationRepository).save(any(UserNotification.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    void sendToSeasonMembers_shouldFanOutToAllActiveMembers() {
        User user1 = activeUser("홍길동", "20240001");
        User user2 = activeUser("김철수", "20240002");

        UserSeason us1 = UserSeason.builder().user(user1).userRoles(Set.of(UserRole.ACTOR)).build();
        UserSeason us2 = UserSeason.builder().user(user2).userRoles(Set.of(UserRole.LEADER)).build();

        when(notificationRepository.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userSeasonRepository.findBySeasonId(1L)).thenReturn(List.of(us1, us2));

        notificationService.sendToSeasonMembers(1L, "공지", "내용",
                NotificationType.ANNOUNCEMENT, SourceType.LMS, 42L);

        ArgumentCaptor<List<UserNotification>> captor = ArgumentCaptor.forClass(List.class);
        verify(userNotificationRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).hasSize(2);
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
        Notification n = Notification.builder()
                .title("공지").content("내용")
                .notificationType(NotificationType.ANNOUNCEMENT)
                .sourceType(SourceType.LMS).sourceId(1L).build();

        UserNotification un1 = new UserNotification(user, n);
        UserNotification un2 = new UserNotification(user, n);

        assertThat(un1.isRead()).isFalse();
        assertThat(un2.isRead()).isFalse();

        when(userNotificationRepository.findByUserIdAndIsReadFalse(any())).thenReturn(List.of(un1, un2));

        notificationService.markAllAsRead(1L);

        assertThat(un1.isRead()).isTrue();
        assertThat(un2.isRead()).isTrue();
    }

    @Test
    void hasUnreadOfType_shouldDelegateToRepository() {
        when(userNotificationRepository.existsByUserIdAndIsReadFalseAndNotification_NotificationType(
                1L, NotificationType.PASSWORD_NOT_CHANGED)).thenReturn(true);

        assertThat(notificationService.hasUnreadOfType(1L, NotificationType.PASSWORD_NOT_CHANGED)).isTrue();
    }
}
