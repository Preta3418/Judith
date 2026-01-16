package com.judtih.judith_management_system.shared.notification.entity;

import com.judtih.judith_management_system.domain.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class UserNotification {

    public UserNotification(User user, Notification notification) {
        this.user = user;
        this.notification = notification;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_id", nullable = false)
    private Notification notification;

    private boolean isRead = false;

    private LocalDateTime readAt;


    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }






}
