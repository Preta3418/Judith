package com.judtih.judith_management_system.shared.notification.service;

import com.judtih.judith_management_system.domain.season.Season;
import com.judtih.judith_management_system.domain.season.SeasonRepository;
import com.judtih.judith_management_system.domain.season.Status;
import com.judtih.judith_management_system.domain.user.entity.User;
import com.judtih.judith_management_system.domain.user.entity.UserSeason;
import com.judtih.judith_management_system.domain.user.repository.UserRepository;
import com.judtih.judith_management_system.domain.user.enums.UserStatus;
import com.judtih.judith_management_system.domain.user.repository.UserSeasonRepository;
import com.judtih.judith_management_system.shared.notification.dto.NotificationResponse;
import com.judtih.judith_management_system.shared.notification.dto.UserNotificationRequest;
import com.judtih.judith_management_system.shared.notification.dto.UserNotificationResponse;
import com.judtih.judith_management_system.shared.notification.entity.Notification;
import com.judtih.judith_management_system.shared.notification.entity.UserNotification;
import com.judtih.judith_management_system.shared.notification.exception.NoNotificationFoundException;
import com.judtih.judith_management_system.shared.notification.repository.NotificationRepository;
import com.judtih.judith_management_system.shared.notification.repository.UserNotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserNotificationRepository userNotificationRepository;
    private final UserSeasonRepository userSeasonRepository;
    private final SeasonRepository seasonRepository;


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

        List<User> targetUsers = new ArrayList<>();

        Optional<Season> activeSeason = seasonRepository.findByStatus(Status.ACTIVE);

        if (activeSeason.isPresent()) {
            List<UserSeason> seasonUsers = userSeasonRepository.findBySeasonId(activeSeason.get().getId());

            for (UserSeason userSeason : seasonUsers) {

                boolean hasTargetRole = !Collections.disjoint(userSeason.getUserRoles(), request.getTargetRoles());

                boolean isActive = userSeason.getUser().getStatus() == UserStatus.ACTIVE;
                boolean notAlreadyAdded = !targetUsers.contains(userSeason.getUser());

                if (hasTargetRole && isActive && notAlreadyAdded) {
                    targetUsers.add(userSeason.getUser());
                }
            }
        }


        List<UserNotification> userNotificationList = new ArrayList<>();

        for(User user : targetUsers) {
            UserNotification userNotification = new UserNotification(user, notification);
            userNotificationList.add(userNotification);
        }

        userNotificationRepository.saveAll(userNotificationList);

        int count = userNotificationList.size();

        return createNotificationResponse(notification, count);

    }

    public List<UserNotificationResponse> getNotificationForUser(Long userId) {
        List<UserNotification> userNotifications = userNotificationRepository.findByUserId(userId);

        List<UserNotificationResponse> userNotificationResponses = new ArrayList<>();

        for (UserNotification notification: userNotifications) {
            UserNotificationResponse response = createUserNotificationResponse(notification);
            userNotificationResponses.add(response);
        }

        return userNotificationResponses;
    }

    public List<UserNotificationResponse> getUnreadNotifications(Long userId) {
        List<UserNotification> userNotifications = userNotificationRepository.findByUserIdAndIsReadFalse(userId);

        List<UserNotificationResponse> userNotificationResponses = new ArrayList<>();

        return userNotifications.stream()
                .map(this::createUserNotificationResponse)
                .toList();
    }


    public int getUnreadCount(Long userId) {
        return userNotificationRepository.countByUserIdAndIsReadFalse(userId);
    }


    @Transactional
    public void markAsRead(Long userNotificationId) {
        UserNotification userNotification = userNotificationRepository.findById(userNotificationId)
                .orElseThrow(() -> new NoNotificationFoundException("No notification was found with id: " + userNotificationId, 404, "Not Found"));

        userNotification.markAsRead();
    }


    @Transactional
    public void markAllAsRead(Long userId) {
        List<UserNotification> userNotifications = userNotificationRepository.findByUserIdAndIsReadFalse(userId);

        for (UserNotification notification : userNotifications) {
            notification.markAsRead();
        }

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
