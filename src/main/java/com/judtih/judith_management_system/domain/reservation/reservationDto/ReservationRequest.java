package com.judtih.judith_management_system.domain.reservation.reservationDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
/** Request body for creating a public reservation; no account required — phone number is the identity key. */
public class ReservationRequest {

    private long eventScheduleId;
    private String name;
    private String phoneNumber;
    private Integer ticketCount;

}
