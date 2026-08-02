package com.judtih.judith_management_system.domain.dashboard.service;

import com.judtih.judith_management_system.domain.dashboard.dto.DashboardNotificationRequest;
import com.judtih.judith_management_system.domain.dashboard.dto.DashboardSeasonResponse;
import com.judtih.judith_management_system.domain.dashboard.exception.NotASeasonMemberException;
import com.judtih.judith_management_system.domain.season.Season;
import com.judtih.judith_management_system.domain.season.SeasonRepository;
import com.judtih.judith_management_system.domain.season.exception.NoSeasonFoundException;
import com.judtih.judith_management_system.domain.user.exception.NoUserSeasonFoundException;
import com.judtih.judith_management_system.domain.season.Status;
import com.judtih.judith_management_system.domain.season.exception.SeasonClosedException;
import com.judtih.judith_management_system.domain.user.enums.UserRole;
import com.judtih.judith_management_system.domain.user.repository.UserRepository;
import com.judtih.judith_management_system.domain.user.repository.UserSeasonRepository;
import com.judtih.judith_management_system.global.notification.dto.NotificationResponse;
import com.judtih.judith_management_system.global.notification.dto.UserNotificationRequest;
import com.judtih.judith_management_system.global.notification.dto.UserNotificationResponse;
import com.judtih.judith_management_system.global.notification.enums.NotificationType;
import com.judtih.judith_management_system.global.notification.enums.SourceType;
import com.judtih.judith_management_system.global.notification.repository.UserNotificationRepository;
import com.judtih.judith_management_system.global.notification.service.NotificationService;
import com.judtih.judith_management_system.global.storage.StorageFolder;
import com.judtih.judith_management_system.global.storage.dto.StoredFileResponse;
import com.judtih.judith_management_system.global.storage.repository.StorageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Orchestrates all member dashboard data. This service has no entity of its own —
 * it aggregates data from Season, UserSeason, StoredFile, and Notification.
 *
 * Access control pattern:
 * Every public method accepts a {@code hasFullAccess} boolean that is determined
 * by the controller from Spring Security (ROLE_ADMIN). The service itself never
 * decides who is admin — it just acts on the flag. When true, membership checks
 * are skipped and the user is treated as if they hold LEADER in every season.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserSeasonRepository userSeasonRepository;
    private final StorageRepository storageRepository;
    private final UserNotificationRepository userNotificationRepository;
    private final NotificationService notificationService;
    private final SeasonRepository seasonRepository;
    private final UserRepository userRepository;


    // Returns all seasons the user participated in, with their roles per season.
    // Admin has no UserSeason rows, so we skip that table entirely and return
    // all seasons from seasonRepository, assigning LEADER role on each.
    public List<DashboardSeasonResponse> getMySeasonsWithDetail(Long userId, boolean hasFullAccess) {
        log.debug("getMySeasonsWithDetail: userId={}, hasFullAccess={}", userId, hasFullAccess);
        if (hasFullAccess) {
            return seasonRepository.findAll().stream()
                    .map(season -> DashboardSeasonResponse.builder()
                            .seasonId(season.getId())
                            .seasonName(season.getName())
                            .status(season.getStatus())
                            .startDate(season.getStartDate())
                            .endDate(season.getEndDate())
                            .eventDate(season.getEventDate())
                            .myRoles(Set.of(UserRole.LEADER))
                            .build())
                    .toList();
        }
        return userSeasonRepository.findByUserId(userId).stream()
                .map(us -> DashboardSeasonResponse.builder()
                        .seasonId(us.getSeason().getId())
                        .seasonName(us.getSeason().getName())
                        .status(us.getSeason().getStatus())
                        .startDate(us.getSeason().getStartDate())
                        .endDate(us.getSeason().getEndDate())
                        .eventDate(us.getSeason().getEventDate())
                        .myRoles(us.getUserRoles())
                        .build())
                .toList();
    }

    // Returns detail for a single season. Verifies membership first (skipped for admin).
    // Role resolution follows the same pattern: admin gets LEADER, member fetches from UserSeason.
    public DashboardSeasonResponse getSeasonForMember(Long userId, Long seasonId, boolean hasFullAccess) {
        log.debug("getSeasonForMember: userId={}, seasonId={}", userId, seasonId);
        assertMembership(userId, seasonId, hasFullAccess);

        Season season = seasonRepository.findById(seasonId)
                .orElseThrow(() -> new NoSeasonFoundException("no season found with seasonId:" + seasonId, 404, "Not Found"));

        Set<UserRole> roles = hasFullAccess
                ? Set.of(UserRole.LEADER)
                : userSeasonRepository.findByUserIdAndSeasonId(userId, seasonId)
                        .orElseThrow(() -> new NoUserSeasonFoundException("No userSeason found with userId:" + userId, 404, "Not Found"))
                        .getUserRoles();

        return DashboardSeasonResponse.builder()
                .seasonId(seasonId)
                .seasonName(season.getName())
                .status(season.getStatus())
                .startDate(season.getStartDate())
                .endDate(season.getEndDate())
                .eventDate(season.getEventDate())
                .myRoles(roles)
                .build();
    }

    // Returns all script files uploaded to this season (StorageFolder.SCRIPT).
    // Upload itself is handled by the existing admin upload endpoint — this is read-only.
    public List<StoredFileResponse> getScriptsForSeason(Long userId, Long seasonId, boolean hasFullAccess) {
        assertMembership(userId, seasonId, hasFullAccess);
        return storageRepository.findBySeasonIdAndFileType(seasonId, StorageFolder.SCRIPT).stream()
                .map(file -> StoredFileResponse.builder()
                        .id(file.getId())
                        .url(file.getUrl())
                        .fileName(file.getFileName())
                        .fileSize(file.getFileSize())
                        .folder(file.getFileType())
                        .uploadedAt(file.getUploadedAt())
                        .build())
                .toList();
    }

    // Returns all notifications delivered to this user that belong to this season (sourceType=LMS, sourceId=seasonId).
    public List<UserNotificationResponse> getSeasonNotifications(Long userId, Long seasonId, boolean hasFullAccess) {
        assertMembership(userId, seasonId, hasFullAccess);
        return userNotificationRepository.findSeasonNotifications(userId, SourceType.LMS, seasonId).stream()
                .map(un -> UserNotificationResponse.builder()
                        .userNotificationId(un.getId())
                        .title(un.getNotification().getTitle())
                        .content(un.getNotification().getContent())
                        .isRead(un.isRead())
                        .readAt(un.getReadAt())
                        .createdAt(un.getNotification().getCreatedAt())
                        .build())
                .toList();
    }

    // Creates an announcement notification and sends it to all members of this season.
    // Season must be ACTIVE — closed seasons are fully read-only.
    // targetRoles=null means NotificationService sends to every member in the season.
    public NotificationResponse createSeasonNotification(Long userId, Long seasonId, DashboardNotificationRequest request, boolean hasFullAccess) {
        log.info("createSeasonNotification: userId={}, seasonId={}, title={}", userId, seasonId, request.getTitle());
        assertMembership(userId, seasonId, hasFullAccess);

        Season season = seasonRepository.findById(seasonId)
                .orElseThrow(() -> new NoSeasonFoundException("no season found with seasonId:" + seasonId, 404, "Not Found"));

        if (season.getStatus() != Status.ACTIVE) {
            throw new SeasonClosedException("Cannot create notifications for a non-active season", 409, "Conflict");
        }

        UserNotificationRequest notificationRequest = UserNotificationRequest.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .notificationType(NotificationType.ANNOUNCEMENT)
                .sourceType(SourceType.LMS)
                .sourceId(seasonId)
                .targetRoles(null)
                .build();

        return notificationService.createNotification(notificationRequest);
    }

    // Central membership gate called by every method above.
    // Admin always passes — they have no UserSeason rows but can access everything.
    private void assertMembership(Long userId, Long seasonId, boolean hasFullAccess) {
        if (hasFullAccess) return;
        if (!userSeasonRepository.existsByUserIdAndSeasonId(userId, seasonId)) {
            throw new NotASeasonMemberException("Not a member of this season");
        }
    }
}
