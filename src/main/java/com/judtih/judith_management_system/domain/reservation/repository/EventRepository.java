package com.judtih.judith_management_system.domain.reservation.repository;

import com.judtih.judith_management_system.domain.reservation.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
}
