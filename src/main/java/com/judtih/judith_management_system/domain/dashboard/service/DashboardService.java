package com.judtih.judith_management_system.domain.dashboard.service;

import com.judtih.judith_management_system.domain.dashboard.dto.DashboardSeasonResponse;
import com.judtih.judith_management_system.domain.dashboard.exception.NotASeasonMemberException;
import com.judtih.judith_management_system.domain.season.Season;
import com.judtih.judith_management_system.domain.season.SeasonRepository;
import com.judtih.judith_management_system.domain.season.Status;
import com.judtih.judith_management_system.domain.season.exception.NoSeasonFoundException;
import com.judtih.judith_management_system.domain.user.entity.UserSeason;
import com.judtih.judith_management_system.domain.user.enums.UserRole;
import com.judtih.judith_management_system.domain.user.repository.UserSeasonRepository;
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
 * Orchestrates member dashboard data. No entity of its own — aggregates Season, UserSeason, StoredFile.
 * Announcement endpoints moved to AnnouncementService/AnnouncementController.
 *
 * Access control pattern:
 * Every public method accepts a {@code hasFullAccess} boolean derived from ROLE_ADMIN in the controller.
 * The service never decides who is admin — it just acts on the flag.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserSeasonRepository userSeasonRepository;
    private final StorageRepository storageRepository;
    private final SeasonRepository seasonRepository;

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

    public DashboardSeasonResponse getSeasonForMember(Long userId, Long seasonId, boolean hasFullAccess) {
        log.debug("getSeasonForMember: userId={}, seasonId={}", userId, seasonId);
        assertReadAccess(userId, seasonId, hasFullAccess);

        Season season = seasonRepository.findById(seasonId)
                .orElseThrow(() -> new NoSeasonFoundException("no season found with seasonId:" + seasonId, 404, "Not Found"));

        Set<UserRole> roles = hasFullAccess
                ? Set.of(UserRole.LEADER)
                : userSeasonRepository.findByUserIdAndSeasonId(userId, seasonId)
                        .map(UserSeason::getUserRoles)
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

    private static final List<Status> CURRENT_SEASON_STATUSES = List.of(Status.ACTIVE, Status.PREPARING);

    private void assertReadAccess(Long userId, Long seasonId, boolean hasFullAccess) {
        if (hasFullAccess) return;
        if (userSeasonRepository.existsByUserIdAndSeasonId(userId, seasonId)) return;
        if (userSeasonRepository.existsByUserIdAndSeason_StatusIn(userId, CURRENT_SEASON_STATUSES)) return;
        throw new NotASeasonMemberException("Not a member of this season");
    }
}
