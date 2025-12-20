package com.judtih.judith_management_system.domain.reservation;


import com.judtih.judith_management_system.domain.reservation.eventDto.EventListResponse;
import com.judtih.judith_management_system.domain.reservation.eventDto.EventRequest;
import com.judtih.judith_management_system.domain.reservation.eventDto.EventResponse;
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
@RequestMapping("/theatre")
@RequiredArgsConstructor
public class ReservationController {

    private final EventService eventService;
    private final ReservationService reservationService;


    //Admin Controller//////////////////////////////////////////////////////////////

    @PostMapping("/admin/events")
    public ResponseEntity<EventResponse> createEvent (@RequestBody EventRequest eventRequest) {
        return ResponseEntity.status(201).body(eventService.createEvent(eventRequest));
    }

    @PutMapping("/admin/events/{id}")
    public ResponseEntity<EventResponse> updateEvent (@PathVariable Long id, @RequestBody EventRequest request) {
        EventResponse response = eventService.updateEvent(id, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/admin/events/{id}")
    public ResponseEntity<Void> deleteEvent (@PathVariable Long id) {
        eventService.deleteEventById(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/admin/events/{id}/reservations")
    public ResponseEntity<List<ReservationResponse>> getAllReservation (@PathVariable Long id) {
        List<ReservationResponse> response = reservationService.getReservationByEventId(id);

        return ResponseEntity.ok(response);

    }


    //User Controller////////////////////////////////////////////////////////////////

    @GetMapping("/events/{id}")
    public ResponseEntity<EventResponse> getEvent (@PathVariable Long id) {
        EventResponse response = eventService.getEventById(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/events")
    public ResponseEntity<List<EventListResponse>> getAllEvent () {
        List<EventListResponse> response = eventService.getAllEvent();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/reservations/lookup")
    public ResponseEntity<ReservationResponse> getReservation (@RequestBody LookUpRequest lookUpRequest) {
        ReservationResponse response = reservationService.getReservation(lookUpRequest.getEventId(), lookUpRequest.getPhoneNumber());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody ReservationRequest request) {
        ReservationResponse response = reservationService.createReservation(request);

        return ResponseEntity.status(201).body(response);
    }

    @DeleteMapping("/reservations")
    public ResponseEntity<Void> deleteReservation(@RequestBody LookUpRequest lookUpRequest) {
        reservationService.deleteReservation(lookUpRequest.getEventId(), lookUpRequest.getPhoneNumber());

        return ResponseEntity.noContent().build();
    }



}
