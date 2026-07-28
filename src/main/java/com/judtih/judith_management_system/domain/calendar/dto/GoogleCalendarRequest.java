package com.judtih.judith_management_system.domain.calendar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleCalendarRequest {
    String title;
    String description;
    LocalDateTime start;
    LocalDateTime end;
    String color;
}
