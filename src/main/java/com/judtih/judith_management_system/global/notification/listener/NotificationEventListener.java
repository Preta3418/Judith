package com.judtih.judith_management_system.global.notification.listener;

import com.judtih.judith_management_system.domain.user.entity.User;
import com.judtih.judith_management_system.global.notification.enums.NotificationType;
import com.judtih.judith_management_system.global.notification.enums.SourceType;
import com.judtih.judith_management_system.global.notification.service.NotificationService;
import com.judtih.judith_management_system.global.security.event.UserLoggedInEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Handles side effects of domain events. AuthController publishes UserLoggedInEvent and stops —
 * this listener decides what happens next. Adding new login side effects means adding a listener, not touching auth.
 */
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;

    @EventListener
    @Transactional
    public void handleUserLogin(UserLoggedInEvent event) {
        User user = event.getUser();

        if (notificationService.hasUnreadOfType(user.getId(), NotificationType.PASSWORD_NOT_CHANGED)) return;

        notificationService.sendToUser(
                user,
                "비밀번호를 변경해 주세요",
                "보안을 위해 비밀번호를 변경하는 것을 권장합니다.",
                NotificationType.PASSWORD_NOT_CHANGED,
                SourceType.AUTH,
                null
        );
    }
}
