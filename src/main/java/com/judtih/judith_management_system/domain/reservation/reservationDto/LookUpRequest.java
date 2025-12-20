package com.judtih.judith_management_system.domain.reservation.reservationDto;


import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LookUpRequest {
    Long eventId;
    String phoneNumber;
}
