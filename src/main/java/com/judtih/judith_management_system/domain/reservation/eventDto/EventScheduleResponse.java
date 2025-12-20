package com.judtih.judith_management_system.domain.reservation.eventDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventScheduleResponse {

    private Long eventScheduleId;
    private Long eventId;
    private LocalDateTime eventDate;
    private LocalDateTime registrationDeadLine;

}
