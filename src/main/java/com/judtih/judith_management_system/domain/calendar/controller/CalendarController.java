package com.judtih.judith_management_system.domain.calendar.controller;

import com.judtih.judith_management_system.domain.calendar.dto.GoogleCalendarRequest;
import com.judtih.judith_management_system.domain.calendar.dto.GoogleCalendarResponse;
import com.judtih.judith_management_system.domain.calendar.service.GoogleCalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CalendarController {

    private final GoogleCalendarService googleCalendarService;

    @Value("${google.calendar.id}")
    private String calendarId;

    // Public — Google Calendar embed URL
    @GetMapping("/api/public/calendar/embed")
    public ResponseEntity<Map<String, String>> getEmbedUrl() {
        String embedUrl = "https://calendar.google.com/calendar/embed?src=" + calendarId
                + "&src=ko.south_korea%23holiday%40group.v.calendar.google.com"
                + "&ctz=Asia%2FSeoul&hl=ko&mode=MONTH";
        return ResponseEntity.ok(Map.of("embedUrl", embedUrl));
    }

    // All members — view events for a date range
    @GetMapping("/api/dashboard/calendar")
    public ResponseEntity<List<GoogleCalendarResponse>> getEvents(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) throws IOException {
        return ResponseEntity.ok(googleCalendarService.getEvents(from, to));
    }

    // Admin only — create event
    @PostMapping("/api/admin/calendar")
    public ResponseEntity<GoogleCalendarResponse> createEvent(
            @RequestBody GoogleCalendarRequest req) throws IOException {
        return ResponseEntity.status(201).body(googleCalendarService.createEvent(req));
    }

    // Admin only — update event
    @PutMapping("/api/admin/calendar/{googleEventId}")
    public ResponseEntity<GoogleCalendarResponse> updateEvent(
            @PathVariable String googleEventId,
            @RequestBody GoogleCalendarRequest req) throws IOException {
        return ResponseEntity.ok(googleCalendarService.updateEvent(googleEventId, req));
    }

    // Admin only — delete event
    @DeleteMapping("/api/admin/calendar/{googleEventId}")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable String googleEventId) throws IOException {
        googleCalendarService.deleteEvent(googleEventId);
        return ResponseEntity.noContent().build();
    }
}
