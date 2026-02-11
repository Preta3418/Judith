package com.judtih.judith_management_system.global.notification;

import com.judtih.judith_management_system.global.notification.dto.NotificationResponse;
import com.judtih.judith_management_system.global.notification.dto.UserNotificationRequest;
import com.judtih.judith_management_system.global.notification.dto.UserNotificationResponse;
import com.judtih.judith_management_system.global.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;


    // ==================== Admin Endpoints ====================

    @PostMapping("/api/admin/notifications")
    public ResponseEntity<NotificationResponse> createNotification(@RequestBody UserNotificationRequest request) {
        return ResponseEntity.status(201).body(service.createNotification(request));
    }


    // ==================== Member Endpoints ====================

    @PreAuthorize("authentication.details == #userId")
    @GetMapping("/api/notifications/{userId}")
    public ResponseEntity<List<UserNotificationResponse>> getNotificationsForUser(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getNotificationForUser(userId));
    }

    @PreAuthorize("authentication.details == #userId")
    @GetMapping("/api/notifications/{userId}/unread")
    public ResponseEntity<List<UserNotificationResponse>> getUnreadNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getUnreadNotifications(userId));
    }

    @PreAuthorize("authentication.details == #userId")
    @GetMapping("/api/notifications/{userId}/unread/count")
    public ResponseEntity<Integer> getUnreadCount(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getUnreadCount(userId));
    }

    @PostMapping("/api/notifications/{userNotificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long userNotificationId) {
        service.markAsRead(userNotificationId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("authentication.details == #userId")
    @PostMapping("/api/notifications/{userId}/read-all")
    public ResponseEntity<Void> markAllAsRead(@PathVariable Long userId) {
        service.markAllAsRead(userId);
        return ResponseEntity.noContent().build();
    }

}
