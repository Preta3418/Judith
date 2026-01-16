package com.judtih.judith_management_system.shared.notification.service;

import com.judtih.judith_management_system.domain.user.User;
import com.judtih.judith_management_system.domain.user.UserRepository;
import com.judtih.judith_management_system.domain.user.UserStatus;
import com.judtih.judith_management_system.shared.notification.dto.NotificationResponse;
import com.judtih.judith_management_system.shared.notification.dto.UserNotificationRequest;
import com.judtih.judith_management_system.shared.notification.dto.UserNotificationResponse;
import com.judtih.judith_management_system.shared.notification.entity.Notification;
import com.judtih.judith_management_system.shared.notification.entity.UserNotification;
import com.judtih.judith_management_system.shared.notification.repository.NotificationRepository;
import com.judtih.judith_management_system.shared.notification.repository.UserNotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserNotificationRepository userNotificationRepository;
    private final UserRepository userRepository;


    @Transactional
    public NotificationResponse createNotification(UserNotificationRequest request) {
        Notification notification = Notification.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .notificationType(request.getNotificationType())
                .sourceType(request.getSourceType())
                .sourceId(request.getSourceId())
                .build();


        notificationRepository.save(notification);

        List<User> targetUsers = userRepository.findByRoleInAndStatus(request.getTargetRoles(), UserStatus.ACTIVE);
        List<UserNotification> userNotificationList = new ArrayList<>();

        for(User user : targetUsers) {
            UserNotification userNotification = new UserNotification(user, notification);
            userNotificationList.add(userNotification);
        }

        userNotificationRepository.saveAll(userNotificationList);

        int count = userNotificationList.size();

        return createNotificationResponse(notification, count);

    }



    //Helper////////////////////////////////////

    private NotificationResponse createNotificationResponse (Notification notification, int count) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .notificationType(notification.getNotificationType())
                .sourceType(notification.getSourceType())
                .recipientCount(count)
                .createdAt(notification.getCreatedAt())
                .build();
    }

    private UserNotificationResponse createUserNotificationResponse(UserNotification userNotification) {
        return UserNotificationResponse.builder()
                .userNotificationId(userNotification.getId())
                .title(userNotification.getNotification().getTitle())
                .content(userNotification.getNotification().getContent())
                .isRead(userNotification.isRead())
                .readAt(userNotification.getReadAt())
                .createdAt(userNotification.getNotification().getCreatedAt())
                .build();
    }

}
