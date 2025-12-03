package com.judtih.judith_management_system.domain.reservation.eventDto;

import com.judtih.judith_management_system.domain.reservation.entity.EventStatus;

import java.time.LocalDateTime;

public class EventListResponse {

    private String title;
    private LocalDateTime eventDate;
    private String posterImageUrl;
    private EventStatus status;
}
