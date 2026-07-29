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

/**
 * Member-facing dashboard endpoints and the admin endpoint for sending season notifications.
 *
 * hasFullAccess pattern: every handler extracts whether the caller has ROLE_ADMIN from
 * Spring Security authorities and passes it to the service as a boolean. The service then
 * skips membership checks and broadens data access accordingly. The security layer (JWT filter)
 * is the single source of truth for who is admin — the controller just reads it, never decides it.
 */
@RestController
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;


    // ==================== Dashboard Endpoints ====================

    // Returns all seasons the caller participated in (or all seasons if admin).
    @GetMapping("/api/dashboard/seasons")
    public ResponseEntity<List<DashboardSeasonResponse>> getMySeasons(Authentication authentication) {
        Long userId = (Long) authentication.getDetails();
        return ResponseEntity.ok(dashboardService.getMySeasonsWithDetail(userId, isFullAccess(authentication)));
    }

    // Returns detail for one season. Membership is verified in the service unless admin.
    @GetMapping("/api/dashboard/seasons/{seasonId}")
    public ResponseEntity<DashboardSeasonResponse> getSeasonDetail(@PathVariable Long seasonId, Authentication authentication) {
        Long userId = (Long) authentication.getDetails();
        return ResponseEntity.ok(dashboardService.getSeasonForMember(userId, seasonId, isFullAccess(authentication)));
    }

    // Returns script files for the season. Read-only; uploads go through the admin upload endpoint.
    @GetMapping("/api/dashboard/seasons/{seasonId}/scripts")
    public ResponseEntity<List<StoredFileResponse>> getScripts(@PathVariable Long seasonId, Authentication authentication) {
        Long userId = (Long) authentication.getDetails();
        return ResponseEntity.ok(dashboardService.getScriptsForSeason(userId, seasonId, isFullAccess(authentication)));
    }

    // Returns notifications delivered to this user that belong to this season.
    @GetMapping("/api/dashboard/seasons/{seasonId}/notifications")
    public ResponseEntity<List<UserNotificationResponse>> getNotifications(@PathVariable Long seasonId, Authentication authentication) {
        Long userId = (Long) authentication.getDetails();
        return ResponseEntity.ok(dashboardService.getSeasonNotifications(userId, seasonId, isFullAccess(authentication)));
    }


    // ==================== Admin Endpoints ====================

    // Sends an announcement to all members of the season. Season must be ACTIVE.
    @PostMapping("/api/admin/seasons/{seasonId}/notifications")
    public ResponseEntity<NotificationResponse> createNotification(@PathVariable Long seasonId,
                                                                    @RequestBody DashboardNotificationRequest request,
                                                                    Authentication authentication) {
        Long userId = (Long) authentication.getDetails();
        return ResponseEntity.status(201).body(dashboardService.createSeasonNotification(userId, seasonId, request, isFullAccess(authentication)));
    }

    // Checks if the caller holds ROLE_ADMIN in their JWT-derived Spring Security authorities.
    private boolean isFullAccess(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
