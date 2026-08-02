package com.judtih.judith_management_system.domain.announcement.controller;

import com.judtih.judith_management_system.domain.announcement.dto.AnnouncementRequest;
import com.judtih.judith_management_system.domain.announcement.dto.AnnouncementResponse;
import com.judtih.judith_management_system.domain.announcement.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @GetMapping("/api/dashboard/seasons/{seasonId}/announcements")
    public ResponseEntity<List<AnnouncementResponse>> getAnnouncements(
            @PathVariable Long seasonId, Authentication authentication) {
        Long userId = (Long) authentication.getDetails();
        return ResponseEntity.ok(announcementService.getAnnouncements(userId, seasonId, isFullAccess(authentication)));
    }

    @PostMapping("/api/admin/seasons/{seasonId}/announcements")
    public ResponseEntity<AnnouncementResponse> createAnnouncement(
            @PathVariable Long seasonId,
            @RequestBody AnnouncementRequest request,
            Authentication authentication) {
        Long userId = (Long) authentication.getDetails();
        return ResponseEntity.status(201).body(
                announcementService.createAnnouncement(userId, seasonId, request, isFullAccess(authentication)));
    }

    @PutMapping("/api/admin/seasons/{seasonId}/announcements/{announcementId}")
    public ResponseEntity<Void> updateAnnouncement(
            @PathVariable Long seasonId,
            @PathVariable Long announcementId,
            @RequestBody AnnouncementRequest request,
            Authentication authentication) {
        announcementService.updateAnnouncement(announcementId, request.getTitle(), request.getContent(), isFullAccess(authentication));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/api/admin/seasons/{seasonId}/announcements/{announcementId}")
    public ResponseEntity<Void> deleteAnnouncement(
            @PathVariable Long seasonId,
            @PathVariable Long announcementId,
            Authentication authentication) {
        announcementService.deleteAnnouncement(announcementId, isFullAccess(authentication));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/api/admin/seasons/{seasonId}/announcements/{announcementId}/pin")
    public ResponseEntity<Void> pinAnnouncement(
            @PathVariable Long seasonId,
            @PathVariable Long announcementId,
            @RequestParam boolean pinned,
            Authentication authentication) {
        announcementService.pinAnnouncement(announcementId, pinned, seasonId, isFullAccess(authentication));
        return ResponseEntity.noContent().build();
    }

    private boolean isFullAccess(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
