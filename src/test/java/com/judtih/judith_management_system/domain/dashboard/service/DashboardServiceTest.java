package com.judtih.judith_management_system.domain.dashboard.service;

import com.judtih.judith_management_system.domain.dashboard.dto.DashboardNotificationRequest;
import com.judtih.judith_management_system.domain.dashboard.dto.DashboardSeasonResponse;
import com.judtih.judith_management_system.domain.dashboard.exception.NotASeasonMemberException;
import com.judtih.judith_management_system.domain.season.Season;
import com.judtih.judith_management_system.domain.season.SeasonRepository;
import com.judtih.judith_management_system.domain.season.Status;
import com.judtih.judith_management_system.domain.season.exception.SeasonClosedException;
import com.judtih.judith_management_system.domain.user.entity.User;
import com.judtih.judith_management_system.domain.user.entity.UserSeason;
import com.judtih.judith_management_system.domain.user.enums.UserRole;
import com.judtih.judith_management_system.domain.user.repository.UserSeasonRepository;
import com.judtih.judith_management_system.global.notification.dto.NotificationResponse;
import com.judtih.judith_management_system.global.notification.repository.UserNotificationRepository;
import com.judtih.judith_management_system.global.notification.service.NotificationService;
import com.judtih.judith_management_system.global.storage.repository.StorageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DashboardServiceTest {

    @Mock private UserSeasonRepository userSeasonRepository;
    @Mock private SeasonRepository seasonRepository;
    @Mock private StorageRepository storageRepository;
    @Mock private UserNotificationRepository userNotificationRepository;
    @Mock private NotificationService notificationService;

    @InjectMocks
    private DashboardService dashboardService;

    //getMySeasonsWithDetail

    @Test
    void getMySeasonsWithDetail_shouldReturnMappedSeasons() {
        Season season = new Season("2025 봄 시즌");
        season.activateSeason();
        User user = User.builder().name("김동아").studentNumber("20231234").password("pw").isAdmin(false).build();
        UserSeason userSeason = UserSeason.builder().user(user).season(season).userRoles(Set.of(UserRole.ACTOR)).build();

        when(userSeasonRepository.findByUserId(1L)).thenReturn(List.of(userSeason));

        List<DashboardSeasonResponse> result = dashboardService.getMySeasonsWithDetail(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSeasonName()).isEqualTo("2025 봄 시즌");
        assertThat(result.get(0).getStatus()).isEqualTo(Status.ACTIVE);
        assertThat(result.get(0).isMyFullAccess()).isFalse();
    }

    @Test
    void getMySeasonsWithDetail_shouldReturnFullAccessTrue_whenLeaderRole() {
        Season season = new Season("2025 봄 시즌");
        User user = User.builder().name("이회장").studentNumber("20231111").password("pw").isAdmin(false).build();
        UserSeason userSeason = UserSeason.builder().user(user).season(season).userRoles(Set.of(UserRole.LEADER)).build();

        when(userSeasonRepository.findByUserId(1L)).thenReturn(List.of(userSeason));

        List<DashboardSeasonResponse> result = dashboardService.getMySeasonsWithDetail(1L);

        assertThat(result.get(0).isMyFullAccess()).isTrue();
    }

    //getSeasonForMember

    @Test
    void getSeasonForMember_shouldThrow_whenNotMember() {
        when(userSeasonRepository.existsByUserIdAndSeasonId(1L, 10L)).thenReturn(false);

        assertThatThrownBy(() -> dashboardService.getSeasonForMember(1L, 10L))
                .isInstanceOf(NotASeasonMemberException.class);
    }

    @Test
    void getSeasonForMember_shouldReturnSeason_whenMember() {
        Season season = new Season("2025 봄 시즌");
        User user = User.builder().name("박배우").studentNumber("20231234").password("pw").isAdmin(false).build();
        UserSeason userSeason = UserSeason.builder().user(user).season(season).userRoles(Set.of(UserRole.ACTOR)).build();

        when(userSeasonRepository.existsByUserIdAndSeasonId(1L, 10L)).thenReturn(true);
        when(seasonRepository.findById(10L)).thenReturn(Optional.of(season));
        when(userSeasonRepository.findByUserIdAndSeasonId(1L, 10L)).thenReturn(Optional.of(userSeason));

        DashboardSeasonResponse result = dashboardService.getSeasonForMember(1L, 10L);

        assertThat(result.getSeasonName()).isEqualTo("2025 봄 시즌");
    }

    //createSeasonNotification

    @Test
    void createSeasonNotification_shouldThrow_whenNotMember() {
        when(userSeasonRepository.existsByUserIdAndSeasonId(1L, 10L)).thenReturn(false);

        assertThatThrownBy(() -> dashboardService.createSeasonNotification(
                1L, 10L, new DashboardNotificationRequest("제목", "내용")))
                .isInstanceOf(NotASeasonMemberException.class);
    }

    @Test
    void createSeasonNotification_shouldThrow_whenSeasonNotActive() {
        Season season = new Season("종료된 시즌");
        season.closeSeason();

        when(userSeasonRepository.existsByUserIdAndSeasonId(1L, 10L)).thenReturn(true);
        when(seasonRepository.findById(10L)).thenReturn(Optional.of(season));

        assertThatThrownBy(() -> dashboardService.createSeasonNotification(
                1L, 10L, new DashboardNotificationRequest("제목", "내용")))
                .isInstanceOf(SeasonClosedException.class);
    }

    @Test
    void createSeasonNotification_shouldSucceed_whenMemberAndSeasonActive() {
        Season season = new Season("진행 중인 시즌");
        season.activateSeason();
        NotificationResponse mockResponse = NotificationResponse.builder().title("제목").content("내용").build();

        when(userSeasonRepository.existsByUserIdAndSeasonId(1L, 10L)).thenReturn(true);
        when(seasonRepository.findById(10L)).thenReturn(Optional.of(season));
        when(notificationService.createNotification(any())).thenReturn(mockResponse);

        NotificationResponse result = dashboardService.createSeasonNotification(
                1L, 10L, new DashboardNotificationRequest("제목", "내용"));

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("제목");
    }
}
