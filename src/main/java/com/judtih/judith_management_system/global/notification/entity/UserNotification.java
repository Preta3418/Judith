package com.judtih.judith_management_system.global.notification.entity;

import com.judtih.judith_management_system.domain.user.entity.User;
import com.judtih.judith_management_system.global.notification.enums.NotificationType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** Per-user notification delivery record. Carries its own title/content so it has no dependency on any content entity. */
@Entity
@Getter
@NoArgsConstructor
public class UserNotification {

    public UserNotification(User user, String title, String content, NotificationType notificationType, Long announcementId) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.notificationType = notificationType;
        this.announcementId = announcementId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    /** Loose FK to Announcement — null for system notifications (password reminder etc.). */
    private Long announcementId;

    private boolean isRead = false;

    private LocalDateTime readAt;

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() { createdAt = LocalDateTime.now(); }

    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }
}
