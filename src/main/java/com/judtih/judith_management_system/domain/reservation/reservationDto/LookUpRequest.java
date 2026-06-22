package com.judtih.judith_management_system.domain.reservation.reservationDto;


import lombok.Builder;
import lombok.Getter;

/** Request body for cancelling a reservation; phone number is used as the anonymous user identifier. */
@Builder
@Getter
public class LookUpRequest {
    Long eventScheduleId;
    String phoneNumber;
}
