package com.judtih.judith_management_system.domain.season;

import com.judtih.judith_management_system.domain.season.dto.SeasonMemberRequest;
import com.judtih.judith_management_system.domain.season.dto.SeasonRequest;
import com.judtih.judith_management_system.domain.season.dto.SeasonResponse;
import com.judtih.judith_management_system.domain.season.exception.*;
import com.judtih.judith_management_system.domain.user.dto.UserSeasonRequest;
import com.judtih.judith_management_system.domain.user.dto.UserSeasonResponse;
import com.judtih.judith_management_system.domain.user.entity.User;
import com.judtih.judith_management_system.domain.user.entity.UserSeason;
import com.judtih.judith_management_system.domain.user.enums.UserRole;
import com.judtih.judith_management_system.domain.user.repository.UserRepository;
import com.judtih.judith_management_system.domain.user.repository.UserSeasonRepository;
import com.judtih.judith_management_system.domain.user.service.UserSeasonService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SeasonServiceTest {

    @Mock
    private SeasonRepository seasonRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserSeasonRepository userSeasonRepository;

    @Mock
    private UserSeasonService userSeasonService;

    @InjectMocks
    private SeasonService seasonService;

    private User leaderUser() {
        return User.builder()
                .name("홍길동")
                .studentNumber("20240001")
                .phoneNumber("01012345678")
                .password("encoded")
                .build();
    }

    private UserSeason leaderUserSeason(Season season) {
        return UserSeason.builder()
                .user(leaderUser())
                .season(season)
                .userRoles(Set.of(UserRole.LEADER))
                .build();
    }

    @Test
    void createSeason_shouldSucceed_whenValid() {
        SeasonMemberRequest memberRequest = mock(SeasonMemberRequest.class);
        when(memberRequest.getUserId()).thenReturn(1L);
        when(memberRequest.getRoles()).thenReturn(Set.of(UserRole.LEADER));

        SeasonRequest request = SeasonRequest.builder()
                .name("2026 정기공연")
                .members(List.of(memberRequest))
                .build();

        when(seasonRepository.existsByStatusNot(Status.CLOSED)).thenReturn(false);
        when(seasonRepository.save(any(Season.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userSeasonService.addUserToSeason(any(UserSeasonRequest.class)))
                .thenReturn(mock(UserSeasonResponse.class));

        SeasonResponse result = seasonService.createSeason(request);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("2026 정기공연");
    }

    @Test
    void createSeason_shouldThrow_whenAnotherSeasonExists() {
        SeasonMemberRequest memberRequest = mock(SeasonMemberRequest.class);
        SeasonRequest request = SeasonRequest.builder()
                .name("2026 정기공연")
                .members(List.of(memberRequest))
                .build();

        when(seasonRepository.existsByStatusNot(Status.CLOSED)).thenReturn(true);

        assertThatThrownBy(() -> seasonService.createSeason(request))
                .isInstanceOf(AlreadyActiveSeasonException.class);
    }

    @Test
    void createSeason_shouldThrow_whenNoFullAccessMember() {
        SeasonMemberRequest memberRequest = mock(SeasonMemberRequest.class);
        when(memberRequest.getRoles()).thenReturn(Set.of(UserRole.ACTOR));

        SeasonRequest request = SeasonRequest.builder()
                .name("2026 정기공연")
                .members(List.of(memberRequest))
                .build();

        when(seasonRepository.existsByStatusNot(Status.CLOSED)).thenReturn(false);

        assertThatThrownBy(() -> seasonService.createSeason(request))
                .isInstanceOf(NoFullAccessMemberFound.class);
    }

    @Test
    void activateSeason_shouldSucceed_whenAllValid() {
        Season season = new Season("2026 정기공연");
        // PREPARING is default status
        UserSeason us = leaderUserSeason(season);

        when(seasonRepository.findById(1L)).thenReturn(Optional.of(season));
        when(userSeasonRepository.findBySeasonId(1L)).thenReturn(List.of(us));

        SeasonResponse result = seasonService.activateSeason(1L);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(Status.ACTIVE);
    }

    @Test
    void activateSeason_shouldThrow_whenAlreadyActive() {
        Season season = new Season("2026 정기공연");
        season.activateSeason();

        when(seasonRepository.findById(1L)).thenReturn(Optional.of(season));

        assertThatThrownBy(() -> seasonService.activateSeason(1L))
                .isInstanceOf(AlreadyActiveSeasonException.class);
    }

    @Test
    void activateSeason_shouldThrow_whenClosed() {
        Season season = new Season("2026 정기공연");
        season.activateSeason();
        season.closeSeason();

        when(seasonRepository.findById(1L)).thenReturn(Optional.of(season));

        assertThatThrownBy(() -> seasonService.activateSeason(1L))
                .isInstanceOf(SeasonClosedException.class);
    }

    @Test
    void activateSeason_shouldThrow_whenMemberHasNoRole() {
        Season season = new Season("2026 정기공연");
        User user = leaderUser();
        UserSeason usNoRole = UserSeason.builder()
                .user(user)
                .season(season)
                .userRoles(Set.of())
                .build();

        when(seasonRepository.findById(1L)).thenReturn(Optional.of(season));
        when(userSeasonRepository.findBySeasonId(1L)).thenReturn(List.of(usNoRole));

        assertThatThrownBy(() -> seasonService.activateSeason(1L))
                .isInstanceOf(NoRoleAssignedException.class);
    }

    @Test
    void closeSeason_shouldSucceed() {
        Season season = new Season("2026 정기공연");
        season.activateSeason();

        when(seasonRepository.findById(1L)).thenReturn(Optional.of(season));

        SeasonResponse result = seasonService.closeSeason(1L);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(Status.CLOSED);
    }

    @Test
    void closeSeason_shouldThrow_whenAlreadyClosed() {
        Season season = new Season("2026 정기공연");
        season.activateSeason();
        season.closeSeason();

        when(seasonRepository.findById(1L)).thenReturn(Optional.of(season));

        assertThatThrownBy(() -> seasonService.closeSeason(1L))
                .isInstanceOf(SeasonClosedException.class);
    }

    @Test
    void closeSeason_shouldThrow_whenPreparing() {
        Season season = new Season("2026 정기공연");
        // PREPARING by default

        when(seasonRepository.findById(1L)).thenReturn(Optional.of(season));

        assertThatThrownBy(() -> seasonService.closeSeason(1L))
                .isInstanceOf(SeasonPreparingException.class);
    }

    @Test
    void reopenSeason_shouldSucceed_whenClosed() {
        Season season = new Season("2026 정기공연");
        season.activateSeason();
        season.closeSeason();

        when(seasonRepository.findById(1L)).thenReturn(Optional.of(season));

        SeasonResponse result = seasonService.reopenSeason(1L);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(Status.ACTIVE);
    }

    @Test
    void reopenSeason_shouldThrow_whenNotClosed() {
        Season season = new Season("2026 정기공연");
        season.activateSeason();

        when(seasonRepository.findById(1L)).thenReturn(Optional.of(season));

        assertThatThrownBy(() -> seasonService.reopenSeason(1L))
                .isInstanceOf(SeasonClosedException.class);
    }

    @Test
    void getCountDown_shouldReturnCountdown_whenEventDateSet() {
        Season season = new Season("2026 정기공연");
        season.activateSeason();
        season.updateSeason(null, LocalDate.now().plusDays(30));

        when(seasonRepository.findByStatus(Status.ACTIVE)).thenReturn(Optional.of(season));

        var result = seasonService.getCountDown();

        assertThat(result).isNotNull();
        assertThat(result.getCountdown()).isGreaterThan(0);
    }

    @Test
    void getCountDown_shouldThrow_whenNoEventDate() {
        Season season = new Season("2026 정기공연");
        season.activateSeason();
        // eventDate remains null

        when(seasonRepository.findByStatus(Status.ACTIVE)).thenReturn(Optional.of(season));

        assertThatThrownBy(() -> seasonService.getCountDown())
                .isInstanceOf(FieldNullException.class);
    }
}
