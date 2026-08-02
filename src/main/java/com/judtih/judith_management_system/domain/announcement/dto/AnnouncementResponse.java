package com.judtih.judith_management_system.domain.announcement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementResponse {
    private Long id;
    private String title;
    private String content;
    private boolean isPinned;
    private boolean isRead;
    private Long userNotificationId;  // null when caller has no UserNotification row (e.g. admin not in season)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
