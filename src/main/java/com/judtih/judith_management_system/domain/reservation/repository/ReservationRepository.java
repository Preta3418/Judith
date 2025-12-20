package com.judtih.judith_management_system.domain.reservation.repository;

import com.judtih.judith_management_system.domain.reservation.entity.Reservation;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Modifying
    @Transactional
    void deleteByEventScheduleId(Long eventScheduleId);

    Optional<Reservation> findByEventScheduleIdAndPhoneNumber(Long eventScheduleId, String phoneNumber);

    boolean existsByEventScheduleIdAndPhoneNumber(Long eventScheduleId, String PhoneNumber);

    List<Reservation> findByEventScheduleId(Long eventScheduleId);

    @Query("SELECT COALESCE(SUM(r.ticketCount), 0) FROM Reservation r WHERE r.eventSchedule.id = :eventScheduleId")
    Integer sumTicketsByEventScheduleId(@Param("eventScheduleId") Long eventScheduleId);
}
