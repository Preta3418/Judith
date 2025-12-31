package com.judtih.judith_management_system.domain.season;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SeasonRepository extends JpaRepository<Season, Long> {

    Optional<Season> findByStatus(Status status);

    boolean existsByStatusNot(Status status);
    boolean existsByStatus(Status status);
}
