package com.judtih.judith_management_system.domain.reservation.reservationDto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse {

    private Long id;
    private Long eventId;
    private Long eventScheduleId;
    private String eventName;
    private String name;
    private Integer ticketCount;
    private String phoneNumber;
    private LocalDateTime reservedAt;
    private LocalDateTime eventDate;

}
