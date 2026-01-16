package com.judtih.judith_management_system.shared.notification.dto;

import com.judtih.judith_management_system.shared.notification.enums.NotificationType;
import com.judtih.judith_management_system.shared.notification.enums.SourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private Long id;
    private String title;
    private String content;
    private NotificationType notificationType;
    private SourceType sourceType;
    private Integer recipientCount;
    private LocalDateTime createdAt;
}
