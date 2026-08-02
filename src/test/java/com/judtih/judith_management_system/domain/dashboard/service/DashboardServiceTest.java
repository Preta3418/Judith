package com.judtih.judith_management_system.domain.dashboard.service;

import com.judtih.judith_management_system.domain.dashboard.dto.DashboardSeasonResponse;
import com.judtih.judith_management_system.domain.dashboard.exception.NotASeasonMemberException;
import com.judtih.judith_management_system.domain.season.Season;
import com.judtih.judith_management_system.domain.season.SeasonRepository;
import com.judtih.judith_management_system.domain.season.Status;
import com.judtih.judith_management_system.domain.user.entity.User;
import com.judtih.judith_management_system.domain.user.entity.UserSeason;
import com.judtih.judith_management_system.domain.user.enums.UserRole;
import com.judtih.judith_management_system.domain.user.repository.UserSeasonRepository;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DashboardServiceTest {

    @Mock private UserSeasonRepository userSeasonRepository;
    @Mock private SeasonRepository seasonRepository;
    @Mock private StorageRepository storageRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void getMySeasonsWithDetail_shouldReturnMappedSeasons() {
        Season season = new Season("2025 봄 시즌");
        season.activateSeason();
        User user = User.builder().name("김동아").studentNumber("20231234").password("pw").isAdmin(false).build();
        UserSeason userSeason = UserSeason.builder().user(user).season(season).userRoles(Set.of(UserRole.ACTOR)).build();

        when(userSeasonRepository.findByUserId(1L)).thenReturn(List.of(userSeason));

        List<DashboardSeasonResponse> result = dashboardService.getMySeasonsWithDetail(1L, false);

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

        List<DashboardSeasonResponse> result = dashboardService.getMySeasonsWithDetail(1L, false);

        assertThat(result.get(0).isMyFullAccess()).isTrue();
    }

    @Test
    void getSeasonForMember_shouldThrow_whenNotMember() {
        when(userSeasonRepository.existsByUserIdAndSeasonId(1L, 10L)).thenReturn(false);

        assertThatThrownBy(() -> dashboardService.getSeasonForMember(1L, 10L, false))
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

        DashboardSeasonResponse result = dashboardService.getSeasonForMember(1L, 10L, false);

        assertThat(result.getSeasonName()).isEqualTo("2025 봄 시즌");
    }

    @Test
    void getMySeasonsWithDetail_shouldReturnAllSeasons_whenFullAccess() {
        Season s1 = new Season("2025 봄 시즌");
        Season s2 = new Season("2025 가을 시즌");
        when(seasonRepository.findAll()).thenReturn(List.of(s1, s2));

        List<DashboardSeasonResponse> result = dashboardService.getMySeasonsWithDetail(1L, true);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).isMyFullAccess()).isTrue();
        assertThat(result.get(0).getMyRoles()).contains(UserRole.LEADER);
    }

    @Test
    void getSeasonForMember_shouldBypassMembershipCheck_whenFullAccess() {
        Season season = new Season("2025 봄 시즌");
        when(seasonRepository.findById(10L)).thenReturn(Optional.of(season));

        DashboardSeasonResponse result = dashboardService.getSeasonForMember(1L, 10L, true);

        assertThat(result.getSeasonName()).isEqualTo("2025 봄 시즌");
        assertThat(result.isMyFullAccess()).isTrue();
        verify(userSeasonRepository, never()).existsByUserIdAndSeasonId(any(), any());
    }
}
