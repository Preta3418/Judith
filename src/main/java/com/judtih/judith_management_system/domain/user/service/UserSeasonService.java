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
import com.judtih.judith_management_system.domain.user.exception.NoUserFoundException;
import com.judtih.judith_management_system.domain.user.exception.NoUserSeasonFoundException;
import com.judtih.judith_management_system.domain.user.exception.UserSeasonAlreadyExistsException;
import com.judtih.judith_management_system.domain.user.repository.UserRepository;
import com.judtih.judith_management_system.domain.user.repository.UserSeasonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;


@Service
@RequiredArgsConstructor
public class UserSeasonService {

    private final UserSeasonRepository userSeasonRepository;
    private final UserRepository userRepository;
    private final SeasonRepository seasonRepository;

    @Transactional
    public UserSeasonResponse addUserToSeason(UserSeasonRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NoUserFoundException("user not found with id:" + request.getUserId(), 404, "Not Found"));

        Season season = seasonRepository.findById(request.getSeasonId())
                .orElseThrow(() -> new NoSeasonFoundException("season not found with id:" + request.getSeasonId(), 404, "Not Found"));

        if (userSeasonRepository.existsByUserIdAndSeasonId(request.getUserId(), request.getSeasonId())) {
            throw new UserSeasonAlreadyExistsException("User already assigned to this season", 409, "Conflict");
        }

        //Season Status safety check
        if (season.getStatus() == Status.CLOSED) {
            throw new NoSeasonFoundException("Cannot add user when season is closed", 400, "Bad Request");
        }

        if (season.getStatus() == Status.ACTIVE) {
            if(request.getRoles() == null || request.getRoles().isEmpty()) throw new NoRoleAssignedException("no roles assigned for user:" + user.getName(), 400, "Bad Request");
        }



        UserSeason userSeason = UserSeason.builder()
                .user(user)
                .season(season)
                .userRoles(request.getRoles())
                .build();

        userSeasonRepository.save(userSeason);

        return createUserSeasonResponse(userSeason);

    }

    @Transactional
    public UserSeasonResponse updateUserRoles(UpdateUserRolesRequest request) {

        UserSeason userSeason = findUserSeasonOrThrow(request.getUserId(), request.getSeasonId());

        userSeason.updateRoles(request.getUserRoles());

        return createUserSeasonResponse(userSeason);
    }

    @Transactional
    public void removeUserFromSeason(Long userId, Long seasonId) {
        UserSeason userSeason = findUserSeasonOrThrow(userId, seasonId);

        userSeasonRepository.delete(userSeason);
    }


    public List<UserSeasonResponse> getUsersBySeason(Long seasonId) {
        return userSeasonRepository.findBySeasonId(seasonId).stream()
                .map(this::createUserSeasonResponse)
                .toList();
    }

    public List<UserSeasonResponse> getSeasonsByUser(Long userId) {
        return userSeasonRepository.findByUserId(userId).stream()
                .map(this::createUserSeasonResponse)
                .toList();
    }

    public boolean hasFullAccessRole(Long userId, Long seasonId) {
        return userSeasonRepository.findByUserIdAndSeasonId(userId, seasonId)
                .map(userSeason -> !Collections.disjoint(userSeason.getUserRoles(), UserRole.FULL_ACCESS_ROLES))
                .orElse(false);
    }



    private UserSeasonResponse createUserSeasonResponse(UserSeason userSeason) {

        return UserSeasonResponse.builder()
                .id(userSeason.getId())
                .userId(userSeason.getUser().getId())
                .userName(userSeason.getUser().getName())
                .seasonId(userSeason.getSeason().getId())
                .seasonName(userSeason.getSeason().getName())
                .roles(userSeason.getUserRoles())
                .joinedAt(userSeason.getJoinedAt())
                .build();

    }

    private UserSeason findUserSeasonOrThrow(Long userId, Long seasonId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NoUserFoundException("user not found with id:" + userId, 404, "Not Found"));

        seasonRepository.findById(seasonId)
                .orElseThrow(() -> new NoSeasonFoundException("season not found with id:" + seasonId, 404, "Not Found"));

        return userSeasonRepository.findByUserIdAndSeasonId(userId, seasonId)
                .orElseThrow(() -> new NoUserSeasonFoundException("user not found with userid and seasonId:" + userId + ", " + seasonId, 404, "Not Found"));
    }

}
