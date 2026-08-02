package com.judtih.judith_management_system.global.notification.dto;


import com.judtih.judith_management_system.global.notification.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
/** Per-user notification entry returned by member-facing notification endpoints, including read state. */
public class UserNotificationResponse {

    private Long userNotificationId;
    private String title;
    private String content;
    private NotificationType notificationType;
    private Long announcementId;
    private boolean isRead;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;

}
