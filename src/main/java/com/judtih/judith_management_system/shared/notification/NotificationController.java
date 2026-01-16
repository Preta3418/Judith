package com.judtih.judith_management_system.shared.notification;

import com.judtih.judith_management_system.shared.notification.dto.NotificationResponse;
import com.judtih.judith_management_system.shared.notification.dto.UserNotificationRequest;
import com.judtih.judith_management_system.shared.notification.dto.UserNotificationResponse;
import com.judtih.judith_management_system.shared.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    //admin specific controller
    @PostMapping
    public ResponseEntity<NotificationResponse> createNotification(@RequestBody UserNotificationRequest request) {
        return ResponseEntity.status(201).body(service.createNotification(request));
    }

    //User controller
    //need to change every userId to auth when auth has been made.
    @GetMapping("/{userId}")
    public ResponseEntity<List<UserNotificationResponse>> getNotificationsForUser(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getNotificationForUser(userId));
    }

    @GetMapping("/{userId}/unread")
    public ResponseEntity<List<UserNotificationResponse>> getUnreadNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getUnreadNotifications(userId));
    }

    @GetMapping("/{userId}/unread/count")
    public ResponseEntity<Integer> getUnreadCount(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getUnreadCount(userId));
    }

    @PostMapping("/{userNotificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long userNotificationId) {
        service.markAsRead(userNotificationId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/read-all")
    public ResponseEntity<Void> markAllAsRead(@PathVariable Long userId) {
        service.markAllAsRead(userId);
        return ResponseEntity.noContent().build();
    }

}
