package com.judtih.judith_management_system.domain.reservation;


import com.judtih.judith_management_system.domain.reservation.eventDto.*;
import com.judtih.judith_management_system.domain.reservation.reservationDto.LookUpRequest;
import com.judtih.judith_management_system.domain.reservation.reservationDto.ReservationRequest;
import com.judtih.judith_management_system.domain.reservation.reservationDto.ReservationResponse;
import com.judtih.judith_management_system.domain.reservation.service.EventService;
import com.judtih.judith_management_system.domain.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/theatre")
@RequiredArgsConstructor
public class ReservationController {

    private final EventService eventService;
    private final ReservationService reservationService;


    //Admin Controller//////////////////////////////////////////////////////////////

    @PostMapping("/admin/events")
    public ResponseEntity<EventResponse> createEvent (@RequestBody EventRequest eventRequest) {
        return ResponseEntity.status(201).body(eventService.createEvent(eventRequest));
    }

    @PostMapping("/admin/schedule")
    public ResponseEntity<EventScheduleResponse> createSchedule (@RequestBody EventScheduleRequest scheduleRequest) {
        return ResponseEntity.status(201).body(eventService.createEventSchedule(scheduleRequest));
    }

    @PutMapping("/admin/events/{eventId}")
    public ResponseEntity<EventResponse> updateEvent (@PathVariable Long eventId, @RequestBody EventRequest request) {
        EventResponse response = eventService.updateEvent(eventId, request);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/admin/schedule/{scheduleId}")
    public ResponseEntity<EventScheduleResponse> updateSchedule (@PathVariable Long scheduleId, @RequestBody EventScheduleRequest scheduleRequest) {
        EventScheduleResponse response = eventService.updateSchedule(scheduleId, scheduleRequest);

        return ResponseEntity.status(200).body(response);
    }

    @DeleteMapping("/admin/events/{eventId}")
    public ResponseEntity<Void> deleteEvent (@PathVariable Long eventId) {
        eventService.deleteEventById(eventId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/admin/schedule/{scheduleId}/reservations")
    public ResponseEntity<List<ReservationResponse>> getAllReservation (@PathVariable Long scheduleId) {
        List<ReservationResponse> response = reservationService.getReservationByEventScheduleId(scheduleId);

        return ResponseEntity.ok(response);

    }


    //User Controller////////////////////////////////////////////////////////////////

    @GetMapping("/events/{eventId}")
    public ResponseEntity<EventResponse> getEvent (@PathVariable Long eventId) {
        EventResponse response = eventService.getEventById(eventId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/events")
    public ResponseEntity<List<EventListResponse>> getAllEvent () {
        List<EventListResponse> response = eventService.getAllEvent();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/reservations/lookup")
    public ResponseEntity<List<ReservationResponse>> getReservation (@RequestParam String phoneNumber) {

        List<ReservationResponse> responses = reservationService.getReservation(phoneNumber);

        return ResponseEntity.ok(responses);
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody ReservationRequest request) {
        ReservationResponse response = reservationService.createReservation(request);

        return ResponseEntity.status(201).body(response);
    }

    @DeleteMapping("/reservations")
    public ResponseEntity<Void> deleteReservation(@RequestBody LookUpRequest lookUpRequest) {
        reservationService.deleteReservation(lookUpRequest.getEventScheduleId(), lookUpRequest.getPhoneNumber());

        return ResponseEntity.noContent().build();
    }



}
