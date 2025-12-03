package com.judtih.judith_management_system.domain.reservation.eventDto;

import com.judtih.judith_management_system.domain.reservation.entity.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventResponse {

    private Long id;
    private String title;
    private String description;
    private LocalDateTime eventDate;
    private String location;
    private Integer capacityLimit;
    private LocalDateTime registrationDeadLine;
    private EventStatus status;
    private String posterImageUrl;
    private LocalDateTime createdAt;
}
