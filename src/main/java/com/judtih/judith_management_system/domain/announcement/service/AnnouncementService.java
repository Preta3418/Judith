package com.judtih.judith_management_system.domain.announcement.service;

import com.judtih.judith_management_system.domain.announcement.dto.AnnouncementRequest;
import com.judtih.judith_management_system.domain.announcement.dto.AnnouncementResponse;
import com.judtih.judith_management_system.domain.announcement.entity.Announcement;
import com.judtih.judith_management_system.domain.announcement.repository.AnnouncementRepository;
import com.judtih.judith_management_system.domain.dashboard.exception.NotASeasonMemberException;
import com.judtih.judith_management_system.domain.season.Season;
import com.judtih.judith_management_system.domain.season.SeasonRepository;
import com.judtih.judith_management_system.domain.season.Status;
import com.judtih.judith_management_system.domain.season.exception.NoSeasonFoundException;
import com.judtih.judith_management_system.domain.season.exception.SeasonClosedException;
import com.judtih.judith_management_system.global.notification.entity.UserNotification;
import com.judtih.judith_management_system.domain.user.repository.UserSeasonRepository;
import com.judtih.judith_management_system.global.notification.enums.NotificationType;
import com.judtih.judith_management_system.global.notification.exception.NoNotificationFoundException;
import com.judtih.judith_management_system.global.notification.repository.UserNotificationRepository;
import com.judtih.judith_management_system.global.notification.service.NotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final UserNotificationRepository userNotificationRepository;
    private final UserSeasonRepository userSeasonRepository;
    private final SeasonRepository seasonRepository;
    private final NotificationService notificationService;

    public List<AnnouncementResponse> getAnnouncements(Long userId, Long seasonId, boolean hasFullAccess) {
        assertReadAccess(userId, seasonId, hasFullAccess);
        List<Announcement> announcements = announcementRepository.findBySeasonIdOrderByIsPinnedDescCreatedAtDesc(seasonId);
        if (announcements.isEmpty()) return List.of();

        List<Long> ids = announcements.stream().map(Announcement::getId).toList();
        Map<Long, UserNotification> readMap = userNotificationRepository
                .findByUserIdAndAnnouncementIdIn(userId, ids)
                .stream().collect(Collectors.toMap(UserNotification::getAnnouncementId, un -> un));

        return announcements.stream().map(a -> {
            UserNotification un = readMap.get(a.getId());
            return AnnouncementResponse.builder()
                    .id(a.getId())
                    .title(a.getTitle())
                    .content(a.getContent())
                    .isPinned(a.isPinned())
                    .isRead(un != null && un.isRead())
                    .userNotificationId(un != null ? un.getId() : null)
                    .createdAt(a.getCreatedAt())
                    .updatedAt(a.getUpdatedAt())
                    .build();
        }).toList();
    }

    @Transactional
    public AnnouncementResponse createAnnouncement(Long userId, Long seasonId, AnnouncementRequest req, boolean hasFullAccess) {
        log.info("createAnnouncement: seasonId={}, title={}", seasonId, req.getTitle());
        assertMembership(userId, seasonId, hasFullAccess);

        Season season = seasonRepository.findById(seasonId)
                .orElseThrow(() -> new NoSeasonFoundException("Season not found", 404, "Not Found"));
        if (season.getStatus() != Status.ACTIVE)
            throw new SeasonClosedException("Cannot create announcements for a non-active season", 409, "Conflict");

        Announcement announcement = announcementRepository.save(Announcement.builder()
                .title(req.getTitle())
                .content(req.getContent())
                .seasonId(seasonId)
                .build());

        notificationService.sendToSeasonMembers(seasonId, req.getTitle(), req.getContent(),
                NotificationType.ANNOUNCEMENT, announcement.getId());

        return AnnouncementResponse.builder()
                .id(announcement.getId())
                .title(announcement.getTitle())
                .content(announcement.getContent())
                .isPinned(false)
                .isRead(false)
                .createdAt(announcement.getCreatedAt())
                .build();
    }

    @Transactional
    public void updateAnnouncement(Long announcementId, String title, String content, boolean hasFullAccess) {
        log.info("updateAnnouncement: id={}", announcementId);
        if (!hasFullAccess) throw new SeasonClosedException("Forbidden", 403, "Forbidden");
        announcementRepository.findById(announcementId)
                .orElseThrow(() -> new NoNotificationFoundException("Announcement not found", 404, "Not Found"))
                .update(title, content);
    }

    @Transactional
    public void deleteAnnouncement(Long announcementId, boolean hasFullAccess) {
        log.info("deleteAnnouncement: id={}", announcementId);
        if (!hasFullAccess) throw new SeasonClosedException("Forbidden", 403, "Forbidden");
        announcementRepository.findById(announcementId)
                .orElseThrow(() -> new NoNotificationFoundException("Announcement not found", 404, "Not Found"));
        userNotificationRepository.deleteByAnnouncementId(announcementId);
        announcementRepository.deleteById(announcementId);
    }

    @Transactional
    public void pinAnnouncement(Long announcementId, boolean pinned, Long seasonId, boolean hasFullAccess) {
        log.info("pinAnnouncement: id={}, pinned={}", announcementId, pinned);
        if (!hasFullAccess) throw new SeasonClosedException("Forbidden", 403, "Forbidden");
        if (pinned) {
            announcementRepository.findBySeasonIdOrderByIsPinnedDescCreatedAtDesc(seasonId)
                    .forEach(Announcement::unpin);
        }
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new NoNotificationFoundException("Announcement not found", 404, "Not Found"));
        if (pinned) announcement.pin(); else announcement.unpin();
    }

    private void assertMembership(Long userId, Long seasonId, boolean hasFullAccess) {
        if (hasFullAccess) return;
        if (!userSeasonRepository.existsByUserIdAndSeasonId(userId, seasonId))
            throw new NotASeasonMemberException("Not a member of this season");
    }

    private static final List<Status> CURRENT_SEASON_STATUSES = List.of(Status.ACTIVE, Status.PREPARING);

    private void assertReadAccess(Long userId, Long seasonId, boolean hasFullAccess) {
        if (hasFullAccess) return;
        if (userSeasonRepository.existsByUserIdAndSeasonId(userId, seasonId)) return;
        if (userSeasonRepository.existsByUserIdAndSeason_StatusIn(userId, CURRENT_SEASON_STATUSES)) return;
        throw new NotASeasonMemberException("Not a member of this season");
    }
}
