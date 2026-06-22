package com.judtih.judith_management_system.domain.dashboard.controller;

import com.judtih.judith_management_system.domain.dashboard.dto.DashboardNotificationRequest;
import com.judtih.judith_management_system.domain.dashboard.dto.DashboardSeasonResponse;
import com.judtih.judith_management_system.domain.dashboard.service.DashboardService;
import com.judtih.judith_management_system.global.notification.dto.NotificationResponse;
import com.judtih.judith_management_system.global.notification.dto.UserNotificationResponse;
import com.judtih.judith_management_system.global.storage.dto.StoredFileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Member-facing dashboard endpoints and the admin endpoint for sending season notifications. */
@RestController
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;


    // ==================== Dashboard Endpoints ====================

    @GetMapping("/api/dashboard/seasons")
    public ResponseEntity<List<DashboardSeasonResponse>> getMySeasons(Authentication authentication) {
        Long userId = (Long) authentication.getDetails();
        return ResponseEntity.ok(dashboardService.getMySeasonsWithDetail(userId));
    }

    @GetMapping("/api/dashboard/seasons/{seasonId}")
    public ResponseEntity<DashboardSeasonResponse> getSeasonDetail(@PathVariable Long seasonId, Authentication authentication) {
        Long userId = (Long) authentication.getDetails();
        return ResponseEntity.ok(dashboardService.getSeasonForMember(userId, seasonId));
    }

    @GetMapping("/api/dashboard/seasons/{seasonId}/scripts")
    public ResponseEntity<List<StoredFileResponse>> getScripts(@PathVariable Long seasonId, Authentication authentication) {
        Long userId = (Long) authentication.getDetails();
        return ResponseEntity.ok(dashboardService.getScriptsForSeason(userId, seasonId));
    }

    @GetMapping("/api/dashboard/seasons/{seasonId}/notifications")
    public ResponseEntity<List<UserNotificationResponse>> getNotifications(@PathVariable Long seasonId, Authentication authentication) {
        Long userId = (Long) authentication.getDetails();
        return ResponseEntity.ok(dashboardService.getSeasonNotifications(userId, seasonId));
    }


    // ==================== Admin Endpoints ====================

    @PostMapping("/api/admin/seasons/{seasonId}/notifications")
    public ResponseEntity<NotificationResponse> createNotification(@PathVariable Long seasonId,
                                                                    @RequestBody DashboardNotificationRequest request,
                                                                    Authentication authentication) {
        Long userId = (Long) authentication.getDetails();
        return ResponseEntity.status(201).body(dashboardService.createSeasonNotification(userId, seasonId, request));
    }
}
