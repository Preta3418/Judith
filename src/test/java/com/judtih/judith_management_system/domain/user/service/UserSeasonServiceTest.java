package com.judtih.judith_management_system.domain.user.service;

import com.judtih.judith_management_system.domain.season.Season;
import com.judtih.judith_management_system.domain.season.SeasonRepository;
import com.judtih.judith_management_system.domain.season.Status;
import com.judtih.judith_management_system.domain.season.exception.NoRoleAssignedException;
import com.judtih.judith_management_system.domain.season.exception.NoSeasonFoundException;
import com.judtih.judith_management_system.domain.user.dto.UpdateUserRolesRequest;
import com.judtih.judith_management_system.domain.user.dto.UserSeasonRequest;
import com.judtih.judith_management_system.domain.user.dto.UserSeasonResponse;
import com.judtih.judith_management_system.domain.user.entity.User;
import com.judtih.judith_management_system.domain.user.entity.UserSeason;
import com.judtih.judith_management_system.domain.user.enums.UserRole;
import com.judtih.judith_management_system.domain.user.exception.UserSeasonAlreadyExistsException;
import com.judtih.judith_management_system.domain.user.repository.UserRepository;
import com.judtih.judith_management_system.domain.user.repository.UserSeasonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserSeasonServiceTest {

    @Mock
    private UserSeasonRepository userSeasonRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SeasonRepository seasonRepository;

    @InjectMocks
    private UserSeasonService userSeasonService;

    private User testUser() {
        return User.builder()
                .name("홍길동")
                .studentNumber("20240001")
                .phoneNumber("01012345678")
                .password("encoded")
                .build();
    }

    private Season preparingSeason() {
        return new Season("2026 정기공연");
        // PREPARING by default
    }

    private Season activeSeason() {
        Season season = new Season("2026 정기공연");
        season.activateSeason();
        return season;
    }

    private Season closedSeason() {
        Season season = new Season("2026 정기공연");
        season.activateSeason();
        season.closeSeason();
        return season;
    }

    @Test
    void addUserToSeason_shouldSucceed_whenPreparingSeason() {
        User user = testUser();
        Season season = preparingSeason();

        UserSeasonRequest request = UserSeasonRequest.builder()
                .userId(1L)
                .seasonId(1L)
                .roles(null)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(seasonRepository.findById(1L)).thenReturn(Optional.of(season));
        when(userSeasonRepository.existsByUserIdAndSeasonId(1L, 1L)).thenReturn(false);
        when(userSeasonRepository.save(any(UserSeason.class))).thenAnswer(inv -> inv.getArgument(0));

        UserSeasonResponse result = userSeasonService.addUserToSeason(request);

        assertThat(result).isNotNull();
        verify(userSeasonRepository).save(any(UserSeason.class));
    }

    @Test
    void addUserToSeason_shouldSucceed_whenActiveSeason_withRoles() {
        User user = testUser();
        Season season = activeSeason();

        UserSeasonRequest request = UserSeasonRequest.builder()
                .userId(1L)
                .seasonId(1L)
                .roles(Set.of(UserRole.ACTOR))
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(seasonRepository.findById(1L)).thenReturn(Optional.of(season));
        when(userSeasonRepository.existsByUserIdAndSeasonId(1L, 1L)).thenReturn(false);
        when(userSeasonRepository.save(any(UserSeason.class))).thenAnswer(inv -> inv.getArgument(0));

        UserSeasonResponse result = userSeasonService.addUserToSeason(request);

        assertThat(result).isNotNull();
        verify(userSeasonRepository).save(any(UserSeason.class));
    }

    @Test
    void addUserToSeason_shouldThrow_whenActiveSeason_withoutRoles() {
        User user = testUser();
        Season season = activeSeason();

        UserSeasonRequest request = UserSeasonRequest.builder()
                .userId(1L)
                .seasonId(1L)
                .roles(Set.of())
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(seasonRepository.findById(1L)).thenReturn(Optional.of(season));
        when(userSeasonRepository.existsByUserIdAndSeasonId(1L, 1L)).thenReturn(false);

        assertThatThrownBy(() -> userSeasonService.addUserToSeason(request))
                .isInstanceOf(NoRoleAssignedException.class);
    }

    @Test
    void addUserToSeason_shouldThrow_whenSeasonClosed() {
        User user = testUser();
        Season season = closedSeason();

        UserSeasonRequest request = UserSeasonRequest.builder()
                .userId(1L)
                .seasonId(1L)
                .roles(Set.of(UserRole.ACTOR))
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(seasonRepository.findById(1L)).thenReturn(Optional.of(season));
        when(userSeasonRepository.existsByUserIdAndSeasonId(1L, 1L)).thenReturn(false);

        assertThatThrownBy(() -> userSeasonService.addUserToSeason(request))
                .isInstanceOf(NoSeasonFoundException.class);
    }

    @Test
    void addUserToSeason_shouldThrow_whenDuplicate() {
        User user = testUser();
        Season season = preparingSeason();

        UserSeasonRequest request = UserSeasonRequest.builder()
                .userId(1L)
                .seasonId(1L)
                .roles(null)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(seasonRepository.findById(1L)).thenReturn(Optional.of(season));
        when(userSeasonRepository.existsByUserIdAndSeasonId(1L, 1L)).thenReturn(true);

        assertThatThrownBy(() -> userSeasonService.addUserToSeason(request))
                .isInstanceOf(UserSeasonAlreadyExistsException.class);
    }

    @Test
    void updateUserRoles_shouldSucceed() {
        User user = testUser();
        Season season = activeSeason();
        UserSeason userSeason = UserSeason.builder()
                .user(user)
                .season(season)
                .userRoles(Set.of(UserRole.ACTOR))
                .build();

        UpdateUserRolesRequest request = UpdateUserRolesRequest.builder()
                .userId(1L)
                .seasonId(1L)
                .userRoles(Set.of(UserRole.LEADER))
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(seasonRepository.findById(1L)).thenReturn(Optional.of(season));
        when(userSeasonRepository.findByUserIdAndSeasonId(1L, 1L)).thenReturn(Optional.of(userSeason));

        UserSeasonResponse result = userSeasonService.updateUserRoles(request);

        assertThat(result).isNotNull();
        assertThat(result.getRoles()).contains(UserRole.LEADER);
    }

    @Test
    void removeUserFromSeason_shouldDeleteUserSeason() {
        User user = testUser();
        Season season = activeSeason();
        UserSeason userSeason = UserSeason.builder()
                .user(user)
                .season(season)
                .userRoles(Set.of(UserRole.ACTOR))
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(seasonRepository.findById(1L)).thenReturn(Optional.of(season));
        when(userSeasonRepository.findByUserIdAndSeasonId(1L, 1L)).thenReturn(Optional.of(userSeason));

        userSeasonService.removeUserFromSeason(1L, 1L);

        verify(userSeasonRepository).delete(userSeason);
    }

    @Test
    void hasFullAccessRole_shouldReturnTrue_whenLeaderRole() {
        User user = testUser();
        Season season = activeSeason();
        UserSeason userSeason = UserSeason.builder()
                .user(user)
                .season(season)
                .userRoles(Set.of(UserRole.LEADER))
                .build();

        when(userSeasonRepository.findByUserIdAndSeasonId(1L, 1L)).thenReturn(Optional.of(userSeason));

        boolean result = userSeasonService.hasFullAccessRole(1L, 1L);

        assertThat(result).isTrue();
    }

    @Test
    void hasFullAccessRole_shouldReturnFalse_whenNoSeasonMembership() {
        when(userSeasonRepository.findByUserIdAndSeasonId(1L, 99L)).thenReturn(Optional.empty());

        boolean result = userSeasonService.hasFullAccessRole(1L, 99L);

        assertThat(result).isFalse();
    }
}
