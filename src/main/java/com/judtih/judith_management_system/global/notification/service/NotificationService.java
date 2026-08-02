package com.judtih.judith_management_system.global.notification.service;

import com.judtih.judith_management_system.domain.user.entity.User;
import com.judtih.judith_management_system.domain.user.entity.UserSeason;
import com.judtih.judith_management_system.domain.user.enums.UserStatus;
import com.judtih.judith_management_system.domain.user.repository.UserSeasonRepository;
import com.judtih.judith_management_system.global.notification.dto.UserNotificationResponse;
import com.judtih.judith_management_system.global.notification.entity.UserNotification;
import com.judtih.judith_management_system.global.notification.enums.NotificationType;
import com.judtih.judith_management_system.global.notification.exception.NoNotificationFoundException;
import com.judtih.judith_management_system.global.notification.repository.UserNotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/** Delivers notifications to users and manages per-user read state. Does not own any content entity. */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final UserNotificationRepository userNotificationRepository;
    private final UserSeasonRepository userSeasonRepository;

    /** Sends a notification to a single user — used for system alerts (password reminder etc.). */
    @Transactional
    public void sendToUser(User user, String title, String content, NotificationType type, Long announcementId) {
        log.info("sendToUser: userId={}, type={}", user.getId(), type);
        userNotificationRepository.save(new UserNotification(user, title, content, type, announcementId));
    }

    /** Fans out a notification to all active members of a season. */
    @Transactional
    public void sendToSeasonMembers(Long seasonId, String title, String content, NotificationType type, Long announcementId) {
        log.info("sendToSeasonMembers: seasonId={}, type={}", seasonId, type);
        List<UserNotification> notifications = userSeasonRepository.findBySeasonId(seasonId).stream()
                .filter(us -> us.getUser().getStatus() == UserStatus.ACTIVE)
                .map(us -> new UserNotification(us.getUser(), title, content, type, announcementId))
                .toList();
        userNotificationRepository.saveAll(notifications);
        log.info("sendToSeasonMembers: delivered to {} users", notifications.size());
    }

    public List<UserNotificationResponse> getNotificationsForUser(Long userId) {
        return userNotificationRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<UserNotificationResponse> getUnreadNotifications(Long userId) {
        return userNotificationRepository.findByUserIdAndIsReadFalse(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    public int getUnreadCount(Long userId) {
        return userNotificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Transactional
    public void markAsRead(Long userNotificationId) {
        log.debug("markAsRead: userNotificationId={}", userNotificationId);
        userNotificationRepository.findById(userNotificationId)
                .orElseThrow(() -> new NoNotificationFoundException("Notification not found", 404, "Not Found"))
                .markAsRead();
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        log.debug("markAllAsRead: userId={}", userId);
        userNotificationRepository.findByUserIdAndIsReadFalse(userId)
                .forEach(UserNotification::markAsRead);
    }

    public boolean hasUnreadOfType(Long userId, NotificationType type) {
        return userNotificationRepository.existsByUserIdAndIsReadFalseAndNotificationType(userId, type);
    }

    private UserNotificationResponse toResponse(UserNotification un) {
        return UserNotificationResponse.builder()
                .userNotificationId(un.getId())
                .title(un.getTitle())
                .content(un.getContent())
                .notificationType(un.getNotificationType())
                .announcementId(un.getAnnouncementId())
                .isRead(un.isRead())
                .readAt(un.getReadAt())
                .createdAt(un.getCreatedAt())
                .build();
    }
}
