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
/** Request body for creating or updating an Event; all fields are optional for partial updates. */
public class EventRequest {

    private String title;
    private String description;
    private String location;
    private Integer capacityLimit;
    private EventStatus status;
    private String posterImageUrl;
    private String pamphletUrl;

}
