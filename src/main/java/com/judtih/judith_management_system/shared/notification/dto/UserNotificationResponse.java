package com.judtih.judith_management_system.shared.notification.dto;


import com.judtih.judith_management_system.shared.notification.enums.NotificationType;
import com.judtih.judith_management_system.shared.notification.enums.SourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserNotificationResponse {

    private Long userNotificationId;
    private String title;
    private String content;
    private NotificationType notificationType;
    private SourceType sourceType;
    private boolean isRead;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;

}
