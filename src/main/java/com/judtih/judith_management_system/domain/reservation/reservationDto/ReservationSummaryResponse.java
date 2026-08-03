package com.judtih.judith_management_system.domain.reservation.reservationDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Member-safe view of one reservation row — no phone number.
 * Used by the member-facing "예약자 보기" list on dashboard/events.html
 * so anyone in the club can see who's coming without exposing personal contact info.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationSummaryResponse {

    private String name;
    private Integer ticketCount;
    private LocalDateTime reservedAt;
}
