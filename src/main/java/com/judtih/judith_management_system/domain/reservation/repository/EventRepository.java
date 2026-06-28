package com.judtih.judith_management_system.domain.reservation.repository;

import com.judtih.judith_management_system.domain.reservation.entity.EventStatus;
import com.judtih.judith_management_system.domain.reservation.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Repository for Event entities; supports filtering by a set of statuses for public listing. */
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByStatusIn(List<EventStatus> statuses);

    Optional<Event> findTopByStatusOrderByCreatedAtDesc(EventStatus status);

    Optional<Event> findTopByOrderByCreatedAtDesc();

}
