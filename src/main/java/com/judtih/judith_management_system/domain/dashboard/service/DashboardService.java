package com.judtih.judith_management_system.domain.dashboard.service;

import com.judtih.judith_management_system.domain.dashboard.dto.DashboardNotificationRequest;
import com.judtih.judith_management_system.domain.dashboard.dto.DashboardSeasonResponse;
import com.judtih.judith_management_system.domain.dashboard.exception.NotASeasonMemberException;
import com.judtih.judith_management_system.domain.season.Season;
import com.judtih.judith_management_system.domain.season.SeasonRepository;
import com.judtih.judith_management_system.domain.season.exception.NoSeasonFoundException;
import com.judtih.judith_management_system.domain.user.entity.UserSeason;
import com.judtih.judith_management_system.domain.user.exception.NoUserSeasonFoundException;
import com.judtih.judith_management_system.domain.season.Status;
import com.judtih.judith_management_system.domain.season.exception.SeasonClosedException;
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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserSeasonRepository userSeasonRepository;
    private final StorageRepository storageRepository;
    private final UserNotificationRepository userNotificationRepository;
    private final NotificationService notificationService;
    private final SeasonRepository seasonRepository;


    public List<DashboardSeasonResponse> getMySeasonsWithDetail(Long userId) {
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

    public DashboardSeasonResponse getSeasonForMember(Long userId, Long seasonId) {
        assertMembership(userId, seasonId);

        Season season = seasonRepository.findById(seasonId)
                .orElseThrow(() -> new NoSeasonFoundException("no season found with seasonId:" + seasonId, 404, "Not Found"));

        UserSeason userSeason = userSeasonRepository.findByUserIdAndSeasonId(userId, seasonId)
                .orElseThrow(() -> new NoUserSeasonFoundException("No userSeason found with userId:" + userId, 404, "Not Found"));

        return DashboardSeasonResponse.builder()
                .seasonId(seasonId)
                .seasonName(season.getName())
                .status(season.getStatus())
                .startDate(season.getStartDate())
                .endDate(season.getEndDate())
                .eventDate(season.getEventDate())
                .myRoles(userSeason.getUserRoles())
                .build();

    }

    public List<StoredFileResponse> getScriptsForSeason(Long userId, Long seasonId) {
        assertMembership(userId, seasonId);
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

    public List<UserNotificationResponse> getSeasonNotifications(Long userId, Long seasonId) {
        assertMembership(userId, seasonId);
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

    public NotificationResponse createSeasonNotification(Long userId, Long seasonId, DashboardNotificationRequest request) {
        assertMembership(userId, seasonId);

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

    private void assertMembership(Long userId, Long seasonId) {
        if (!userSeasonRepository.existsByUserIdAndSeasonId(userId, seasonId)) {
            throw new NotASeasonMemberException("Not a member of this season");
        }
    }
}
