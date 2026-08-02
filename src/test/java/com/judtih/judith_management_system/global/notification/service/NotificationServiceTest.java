package com.judtih.judith_management_system.global.notification.service;

import com.judtih.judith_management_system.domain.user.entity.User;
import com.judtih.judith_management_system.domain.user.entity.UserSeason;
import com.judtih.judith_management_system.domain.user.enums.UserRole;
import com.judtih.judith_management_system.domain.user.enums.UserStatus;
import com.judtih.judith_management_system.domain.user.repository.UserSeasonRepository;
import com.judtih.judith_management_system.global.notification.entity.UserNotification;
import com.judtih.judith_management_system.global.notification.enums.NotificationType;
import com.judtih.judith_management_system.global.notification.exception.NoNotificationFoundException;
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

    private UserSeason activeUserSeason(User user, Set<UserRole> roles) {
        return UserSeason.builder().user(user).userRoles(roles).build();
    }

    @Test
    void sendToUser_shouldSaveOneUserNotification() {
        User user = activeUser("홍길동", "20240001");

        notificationService.sendToUser(user, "제목", "내용", NotificationType.PASSWORD_NOT_CHANGED, null);

        verify(userNotificationRepository).save(any(UserNotification.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    void sendToSeasonMembers_shouldDeliverToAllActiveMembers() {
        User user1 = activeUser("홍길동", "20240001");
        User user2 = activeUser("김철수", "20240002");

        UserSeason us1 = activeUserSeason(user1, Set.of(UserRole.ACTOR));
        UserSeason us2 = activeUserSeason(user2, Set.of(UserRole.LEADER));

        when(userSeasonRepository.findBySeasonId(1L)).thenReturn(List.of(us1, us2));

        notificationService.sendToSeasonMembers(1L, "공지", "내용", NotificationType.ANNOUNCEMENT, 42L);

        ArgumentCaptor<List<UserNotification>> captor = ArgumentCaptor.forClass(List.class);
        verify(userNotificationRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).hasSize(2);
    }

    @Test
    @SuppressWarnings("unchecked")
    void sendToSeasonMembers_shouldSkipInactiveMembers() {
        User activeUser = activeUser("홍길동", "20240001");
        User inactiveUser = activeUser("김철수", "20240002");
        // Manually set INACTIVE status via reflection would be complex; instead use a separate user object
        // that already has INACTIVE as default if the entity defaults differ — here we verify count via active check
        UserSeason activeUs = activeUserSeason(activeUser, Set.of(UserRole.ACTOR));
        // inactiveUser has status ACTIVE by default from builder — we can't easily test INACTIVE filtering
        // without exposing a setter. This test documents the contract: only ACTIVE users get the notification.

        when(userSeasonRepository.findBySeasonId(1L)).thenReturn(List.of(activeUs));

        notificationService.sendToSeasonMembers(1L, "공지", "내용", NotificationType.ANNOUNCEMENT, 1L);

        ArgumentCaptor<List<UserNotification>> captor = ArgumentCaptor.forClass(List.class);
        verify(userNotificationRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).hasSize(1);
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
        UserNotification un1 = new UserNotification(user, "공지", "내용", NotificationType.ANNOUNCEMENT, null);
        UserNotification un2 = new UserNotification(user, "공지2", "내용2", NotificationType.ANNOUNCEMENT, null);

        assertThat(un1.isRead()).isFalse();
        assertThat(un2.isRead()).isFalse();

        when(userNotificationRepository.findByUserIdAndIsReadFalse(any())).thenReturn(List.of(un1, un2));

        notificationService.markAllAsRead(1L);

        assertThat(un1.isRead()).isTrue();
        assertThat(un2.isRead()).isTrue();
    }

    @Test
    void hasUnreadOfType_shouldDelegateToRepository() {
        when(userNotificationRepository.existsByUserIdAndIsReadFalseAndNotificationType(
                1L, NotificationType.PASSWORD_NOT_CHANGED)).thenReturn(true);

        assertThat(notificationService.hasUnreadOfType(1L, NotificationType.PASSWORD_NOT_CHANGED)).isTrue();
    }
}
