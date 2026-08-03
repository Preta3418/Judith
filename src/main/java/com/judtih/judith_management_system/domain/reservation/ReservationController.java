package com.judtih.judith_management_system.domain.reservation;


import com.judtih.judith_management_system.domain.reservation.eventDto.*;
import com.judtih.judith_management_system.domain.reservation.reservationDto.LookUpRequest;
import com.judtih.judith_management_system.domain.reservation.reservationDto.ReservationRequest;
import com.judtih.judith_management_system.domain.reservation.reservationDto.ReservationResponse;
import com.judtih.judith_management_system.domain.reservation.service.EventService;
import com.judtih.judith_management_system.domain.reservation.service.ReservationService;
import com.judtih.judith_management_system.global.download.FileDownloadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/** Handles public ticketing (lookup/create/cancel), member event listing, and admin event/schedule management. */
@RestController
@RequiredArgsConstructor
public class ReservationController {

    private final EventService eventService;
    private final ReservationService reservationService;
    private final FileDownloadService fileDownloadService;


    // ==================== Public Endpoints ====================

    @GetMapping("/api/public/events/latest")
    public ResponseEntity<EventResponse> getLatestEvent() {
        try {
            return ResponseEntity.ok(eventService.getLatestEvent());
        } catch (RuntimeException e) {
            return ResponseEntity.noContent().build();
        }
    }

    // Streams the pamphlet through our origin so iOS Safari downloads it properly — see FileDownloadService.
    @GetMapping("/api/public/events/{eventId}/pamphlet/download")
    public ResponseEntity<byte[]> downloadPamphlet(@PathVariable Long eventId) throws IOException {
        String pamphletUrl = eventService.getPamphletUrl(eventId);
        return fileDownloadService.buildDownloadResponse(
                pamphletUrl, "26_1 <물리학자들: Die Physiker> 공연 팸플릿.pdf", MediaType.APPLICATION_PDF);
    }

    @GetMapping("/api/public/events/{eventId}")
    public ResponseEntity<EventResponse> getEvent (@PathVariable Long eventId) {
        EventResponse response = eventService.getEventById(eventId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/public/reservations")
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody ReservationRequest request) {
        ReservationResponse response = reservationService.createReservation(request);

        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/api/public/reservations/lookup")
    public ResponseEntity<List<ReservationResponse>> getReservation (@RequestParam String phoneNumber) {

        List<ReservationResponse> responses = reservationService.getReservation(phoneNumber);

        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/api/public/reservations")
    public ResponseEntity<Void> deleteReservation(@RequestBody LookUpRequest lookUpRequest) {
        reservationService.deleteReservation(lookUpRequest.getEventScheduleId(), lookUpRequest.getPhoneNumber());

        return ResponseEntity.noContent().build();
    }


    // ==================== Member Endpoints ====================

    @GetMapping("/api/events")
    public ResponseEntity<List<EventListResponse>> getAllEvent () {
        List<EventListResponse> response = eventService.getAllEvent();

        return ResponseEntity.ok(response);
    }

    /** Member view of the event for a given season — same service call as the admin endpoint,
     *  but reachable under /api/dashboard/** so any authenticated user can see it. */
    @GetMapping("/api/dashboard/seasons/{seasonId}/event")
    public ResponseEntity<EventResponse> getSeasonEvent(@PathVariable Long seasonId) {
        return eventService.getEventBySeasonId(seasonId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    /** Member-safe reservation list for a schedule — names and ticket counts only.
     *  Phone numbers stay in the admin endpoint (/api/admin/schedule/{id}/reservations). */
    @GetMapping("/api/dashboard/schedule/{scheduleId}/reservations")
    public ResponseEntity<List<com.judtih.judith_management_system.domain.reservation.reservationDto.ReservationSummaryResponse>>
    getScheduleReservationSummaries(@PathVariable Long scheduleId) {
        return ResponseEntity.ok(reservationService.getReservationSummariesByScheduleId(scheduleId));
    }


    // ==================== Admin Endpoints ====================

    @PostMapping(value = "/api/admin/events/{eventId}/pamphlet", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EventResponse> uploadPamphlet(
            @PathVariable Long eventId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("seasonId") Long seasonId) {
        return ResponseEntity.ok(eventService.uploadPamphlet(eventId, file, seasonId));
    }

    @PostMapping("/api/admin/events")
    public ResponseEntity<EventResponse> createEvent (@RequestBody EventRequest eventRequest) {
        return ResponseEntity.status(201).body(eventService.createEvent(eventRequest));
    }

    @PutMapping("/api/admin/events/{eventId}")
    public ResponseEntity<EventResponse> updateEvent (@PathVariable Long eventId, @RequestBody EventRequest request) {
        EventResponse response = eventService.updateEvent(eventId, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/api/admin/events/{eventId}")
    public ResponseEntity<Void> deleteEvent (@PathVariable Long eventId) {
        eventService.deleteEventById(eventId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/admin/schedule")
    public ResponseEntity<EventScheduleResponse> createSchedule (@RequestBody EventScheduleRequest scheduleRequest) {
        return ResponseEntity.status(201).body(eventService.createEventSchedule(scheduleRequest));
    }

    @PutMapping("/api/admin/schedule/{scheduleId}")
    public ResponseEntity<EventScheduleResponse> updateSchedule (@PathVariable Long scheduleId, @RequestBody EventScheduleRequest scheduleRequest) {
        EventScheduleResponse response = eventService.updateSchedule(scheduleId, scheduleRequest);

        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/api/admin/schedule/{scheduleId}/reservations")
    public ResponseEntity<List<ReservationResponse>> getAllReservation (@PathVariable Long scheduleId) {
        List<ReservationResponse> response = reservationService.getReservationByEventScheduleId(scheduleId);

        return ResponseEntity.ok(response);

    }

    @PostMapping("/api/admin/events/{eventId}/close")
    public ResponseEntity<EventResponse> closeEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventService.closeEvent(eventId));
    }

    @GetMapping("/api/admin/events/season/{seasonId}")
    public ResponseEntity<EventResponse> getEventBySeason(@PathVariable Long seasonId) {
        return eventService.getEventBySeasonId(seasonId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }


}
