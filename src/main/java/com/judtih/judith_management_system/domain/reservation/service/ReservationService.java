package com.judtih.judith_management_system.domain.reservation.service;

import com.judtih.judith_management_system.domain.reservation.entity.Event;
import com.judtih.judith_management_system.domain.reservation.entity.EventStatus;
import com.judtih.judith_management_system.domain.reservation.entity.Reservation;
import com.judtih.judith_management_system.domain.reservation.repository.EventRepository;
import com.judtih.judith_management_system.domain.reservation.repository.ReservationRepository;
import com.judtih.judith_management_system.domain.reservation.reservationDto.ReservationRequest;
import com.judtih.judith_management_system.domain.reservation.reservationDto.ReservationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final EventRepository eventRepository;


    //Admin Method ///////////////////////////////////////////////////////////////
    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservationByEventId(Long eventId) {

        List<Reservation> reservations = reservationRepository.findByEventId(eventId);
        List<ReservationResponse> responseList = new ArrayList<>();

        for(Reservation reservation : reservations) {
            responseList.add(createReservationResponse(reservation));
        }

        return responseList;
    }

    //User Method //////////////////////////////////////////////////////////////////////////

    @Transactional
    public ReservationResponse createReservation(ReservationRequest request) {
        Event event = eventRepository.findByIdWithLock(request.getEventId())
                .orElseThrow(() -> new RuntimeException("event not found"));

        if (event.getStatus() != EventStatus.OPEN) {
            throw new RuntimeException("closed event");
        }

        if (LocalDateTime.now().isAfter(event.getRegistrationDeadline())) {
            throw new RuntimeException("event over deadline");
        }

        if (reservationRepository.existsByEventIdAndPhoneNumber(event.getId(), request.getPhoneNumber())) {
            throw new RuntimeException("cannot make another reservation by same user");
        }


        Integer currentCount = reservationRepository.sumConfirmedGuestsByEventId(event.getId());
        if (currentCount + request.getTicketCount() > event.getCapacityLimit()) {
            throw new RuntimeException("No reservation left");
        }


        Reservation reservation = Reservation.builder()
                .event(event)
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .ticketCount(request.getTicketCount())
                .build();

        reservationRepository.save(reservation);

        return createReservationResponse(reservation);
    }

    @Transactional(readOnly = true)
    public ReservationResponse getReservation(Long eventId, String phoneNumber) {
        Reservation reservation = reservationRepository.findByEventIdAndPhoneNumber(eventId, phoneNumber)
                .orElseThrow(() -> new RuntimeException("no reservation found"));

        return createReservationResponse(reservation);
    }

    @Transactional
    public void deleteReservation(Long eventId, String phoneNumber) {
        Reservation reservation = reservationRepository.findByEventIdAndPhoneNumber(eventId, phoneNumber)
                .orElseThrow(() -> new RuntimeException("no reservation found"));

        reservationRepository.deleteById(reservation.getId());
    }



    //Helper Method ///////////////////////////////////////////////////////////////////
    private ReservationResponse createReservationResponse (Reservation reservation) {
        return ReservationResponse.builder()
                .id(reservation.getId())
                .eventId(reservation.getEvent().getId())
                .eventName(reservation.getEvent().getTitle())
                .name(reservation.getName())
                .ticketCount(reservation.getTicketCount())
                .phoneNumber((reservation.getPhoneNumber()))
                .reservedAt(reservation.getReservedAt())
                .build();
    }

}
