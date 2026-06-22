package com.judtih.judith_management_system.domain.reservation.eventDto;

import lombok.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
/** Request body for creating or updating an EventSchedule (a single showing of an event). */
public class EventScheduleRequest {

    private Long eventId;
    private LocalDateTime eventDate;
    private LocalDateTime registrationDeadLine;
}
