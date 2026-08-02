package com.judtih.judith_management_system.domain.dashboard.controller;

import com.judtih.judith_management_system.domain.dashboard.dto.DashboardSeasonResponse;
import com.judtih.judith_management_system.domain.dashboard.service.DashboardService;
import com.judtih.judith_management_system.global.storage.dto.StoredFileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Member-facing dashboard endpoints. Announcement endpoints live in AnnouncementController.
 *
 * hasFullAccess pattern: every handler reads ROLE_ADMIN from Spring Security authorities and passes it
 * to the service as a boolean. The JWT filter is the single source of truth; the controller just reads it.
 */
@RestController
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/api/dashboard/seasons")
    public ResponseEntity<List<DashboardSeasonResponse>> getMySeasons(Authentication authentication) {
        Long userId = (Long) authentication.getDetails();
        return ResponseEntity.ok(dashboardService.getMySeasonsWithDetail(userId, isFullAccess(authentication)));
    }

    @GetMapping("/api/dashboard/seasons/{seasonId}")
    public ResponseEntity<DashboardSeasonResponse> getSeasonDetail(@PathVariable Long seasonId, Authentication authentication) {
        Long userId = (Long) authentication.getDetails();
        return ResponseEntity.ok(dashboardService.getSeasonForMember(userId, seasonId, isFullAccess(authentication)));
    }

    @GetMapping("/api/dashboard/seasons/{seasonId}/scripts")
    public ResponseEntity<List<StoredFileResponse>> getScripts(@PathVariable Long seasonId, Authentication authentication) {
        Long userId = (Long) authentication.getDetails();
        return ResponseEntity.ok(dashboardService.getScriptsForSeason(userId, seasonId, isFullAccess(authentication)));
    }

    private boolean isFullAccess(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
