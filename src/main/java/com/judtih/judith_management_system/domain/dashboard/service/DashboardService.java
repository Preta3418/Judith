package com.judtih.judith_management_system.domain.dashboard.service;

import com.judtih.judith_management_system.domain.dashboard.dto.DashboardNotificationRequest;
import com.judtih.judith_management_system.domain.dashboard.dto.DashboardSeasonResponse;
import com.judtih.judith_management_system.domain.dashboard.exception.NotASeasonMemberException;
import com.judtih.judith_management_system.domain.season.Season;
import com.judtih.judith_management_system.domain.season.SeasonRepository;
import com.judtih.judith_management_system.domain.season.exception.NoSeasonFoundException;
import com.judtih.judith_management_system.domain.user.entity.UserSeason;
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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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


    // Returns seasons visible to this user.
    // Admin sees all seasons as LEADER.
    // Current active season members see ALL seasons (own with real roles, others read-only with empty roles).
    // Past-only members see only their own seasons.
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

        List<UserSeason> mySeasons = userSeasonRepository.findByUserId(userId);
        boolean isCurrentMember = userSeasonRepository.existsByUserIdAndSeason_StatusIn(userId, List.of(Status.ACTIVE, Status.PREPARING));

        if (isCurrentMember) {
            Map<Long, Set<UserRole>> roleMap = mySeasons.stream()
                    .collect(Collectors.toMap(us -> us.getSeason().getId(), us -> us.getUserRoles()));
            return seasonRepository.findAll().stream()
                    .map(season -> DashboardSeasonResponse.builder()
                            .seasonId(season.getId())
                            .seasonName(season.getName())
                            .status(season.getStatus())
                            .startDate(season.getStartDate())
                            .endDate(season.getEndDate())
                            .eventDate(season.getEventDate())
                            .myRoles(roleMap.getOrDefault(season.getId(), Set.of()))
                            .build())
                    .toList();
        }

        return mySeasons.stream()
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

    // Returns detail for a single season. Uses assertReadAccess — member need not be in this season
    // if they are currently in the active season (cross-season read access).
    // Role resolution: admin → LEADER, own season → real roles, cross-season viewer → empty set.
    public DashboardSeasonResponse getSeasonForMember(Long userId, Long seasonId, boolean hasFullAccess) {
        log.debug("getSeasonForMember: userId={}, seasonId={}", userId, seasonId);
        assertReadAccess(userId, seasonId, hasFullAccess);

        Season season = seasonRepository.findById(seasonId)
                .orElseThrow(() -> new NoSeasonFoundException("no season found with seasonId:" + seasonId, 404, "Not Found"));

        Set<UserRole> roles = hasFullAccess
                ? Set.of(UserRole.LEADER)
                : userSeasonRepository.findByUserIdAndSeasonId(userId, seasonId)
                        .map(us -> us.getUserRoles())
                        .orElse(Set.of());

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
        assertReadAccess(userId, seasonId, hasFullAccess);
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
        assertReadAccess(userId, seasonId, hasFullAccess);
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

    // Write gate — caller must be a member of this exact season. Used for notification creation.
    private void assertMembership(Long userId, Long seasonId, boolean hasFullAccess) {
        if (hasFullAccess) return;
        if (!userSeasonRepository.existsByUserIdAndSeasonId(userId, seasonId)) {
            throw new NotASeasonMemberException("Not a member of this season");
        }
    }

    // Read gate — passes if: admin, own season member, or member of any current season (ACTIVE or PREPARING).
    // PREPARING included so members added before activation keep cross-season access during transition gaps.
    private static final List<Status> CURRENT_SEASON_STATUSES = List.of(Status.ACTIVE, Status.PREPARING);

    private void assertReadAccess(Long userId, Long seasonId, boolean hasFullAccess) {
        if (hasFullAccess) return;
        if (userSeasonRepository.existsByUserIdAndSeasonId(userId, seasonId)) return;
        if (userSeasonRepository.existsByUserIdAndSeason_StatusIn(userId, CURRENT_SEASON_STATUSES)) return;
        throw new NotASeasonMemberException("Not a member of this season");
    }
}
