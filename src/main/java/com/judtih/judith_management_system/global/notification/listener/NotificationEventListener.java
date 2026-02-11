package com.judtih.judith_management_system.global.notification.listener;

import com.judtih.judith_management_system.domain.user.entity.User;
import com.judtih.judith_management_system.global.notification.entity.Notification;
import com.judtih.judith_management_system.global.notification.enums.NotificationType;
import com.judtih.judith_management_system.global.notification.enums.SourceType;
import com.judtih.judith_management_system.global.notification.repository.UserNotificationRepository;
import com.judtih.judith_management_system.global.notification.service.NotificationService;
import com.judtih.judith_management_system.global.security.event.UserLoggedInEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;
    private final UserNotificationRepository userNotificationRepository;

    @EventListener
    @Transactional
    public void handleUserLogin(UserLoggedInEvent event) {
        User user = event.getUser();

        boolean alreadyNotified = userNotificationRepository.existsByUserIdAndIsReadFalseAndNotification_NotificationType(
                user.getId(), NotificationType.PASSWORD_NOT_CHANGED);

        if (alreadyNotified) return;

        Notification notification = Notification.builder()
                .title("비밀번호를 변경해 주세요")
                .content("보안을 위해 비밀번호를 변경하는 것을 권장합니다.")
                .notificationType(NotificationType.PASSWORD_NOT_CHANGED)
                .sourceType(SourceType.AUTH)
                .build();

        notificationService.createNotificationForOneUser(user, notification);
    }
}
