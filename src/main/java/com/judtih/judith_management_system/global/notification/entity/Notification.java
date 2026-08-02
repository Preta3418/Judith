package com.judtih.judith_management_system.global.notification.entity;

import com.judtih.judith_management_system.global.notification.enums.NotificationType;
import com.judtih.judith_management_system.global.notification.enums.SourceType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** Bell-delivery record; a single Notification can fan out to many users via UserNotification join rows. Content is owned by the source entity (e.g., Announcement); title/content here are denormalized copies for bell display. */
@Entity
@Getter
@NoArgsConstructor
public class Notification {

    @Builder
    public Notification(String title, String content, NotificationType notificationType, SourceType sourceType, Long sourceId) {
        this.title = title;
        this.content = content;
        this.notificationType = notificationType;
        this.sourceType = sourceType;
        this.sourceId = sourceId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    @Enumerated(EnumType.STRING)
    private SourceType sourceType;

    /** ID of the source record — announcementId when sourceType=LMS; null for AUTH (password reminder). */
    private Long sourceId;

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
