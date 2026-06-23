package com.judtih.judith_management_system.domain.reservation.eventDto;

import com.judtih.judith_management_system.domain.reservation.entity.EventStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
/** Full event detail including all schedules and remaining seats per showing. */
public class EventResponse {

    private Long id;
    private String title;
    private String description;
    private String location;
    private Integer capacityLimit;
    private EventStatus status;
    private String posterImageUrl;
    private LocalDateTime createdAt;

    private List<EventScheduleResponse> schedules;
}
