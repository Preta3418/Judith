package com.judtih.judith_management_system.domain.user.repository;

import com.judtih.judith_management_system.domain.user.entity.UserSeason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSeasonRepository extends JpaRepository<UserSeason, Long> {

    List<UserSeason> findBySeasonId(Long seasonId);

    List<UserSeason> findByUserId(Long userId);

    Optional<UserSeason> findByUserIdAndSeasonId(Long userId, Long seasonId);

    boolean existsByUserIdAndSeasonId(Long userId, Long seasonId);
}
