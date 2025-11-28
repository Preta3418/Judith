package com.judtih.judith_management_system.domain.reservation.repository;

import com.judtih.judith_management_system.domain.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
