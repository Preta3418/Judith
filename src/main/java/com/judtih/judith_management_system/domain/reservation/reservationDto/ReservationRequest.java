package com.judtih.judith_management_system.domain.reservation.reservationDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationRequest {

    private long eventScheduleId;
    private String name;
    private String phoneNumber;
    private Integer ticketCount;

}
