package com.judtih.judith_management_system.domain.season;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/** Repository for Season; includes status-based finders used by access-control and effective-season logic. */
@Repository
public interface SeasonRepository extends JpaRepository<Season, Long> {

    Optional<Season> findByStatus(Status status);

    boolean existsByStatusNot(Status status);
    boolean existsByStatus(Status status);
    Optional<Season> findTopByStatusOrderByCreatedAtDesc(Status status);

}
