package com.judtih.judith_management_system.global.notification.dto;


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
    private boolean isRead;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;

}
