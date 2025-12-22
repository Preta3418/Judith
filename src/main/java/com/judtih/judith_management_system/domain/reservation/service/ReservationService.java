package com.judtih.judith_management_system.domain.reservation.service;

import com.judtih.judith_management_system.domain.reservation.entity.EventSchedule;
import com.judtih.judith_management_system.domain.reservation.entity.EventStatus;
import com.judtih.judith_management_system.domain.reservation.entity.Reservation;
import com.judtih.judith_management_system.domain.reservation.repository.EventScheduleRepository;
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
    private final EventScheduleRepository eventScheduleRepository;


    //Admin Method ///////////////////////////////////////////////////////////////
    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservationByEventScheduleId(Long scheduleId) {

        List<Reservation> reservations = reservationRepository.findByEventScheduleId(scheduleId);
        List<ReservationResponse> responseList = new ArrayList<>();

        for(Reservation reservation : reservations) {
            responseList.add(createReservationResponse(reservation));
        }

        return responseList;
    }

    //User Method //////////////////////////////////////////////////////////////////////////

    @Transactional
    public ReservationResponse createReservation(ReservationRequest request) {
        EventSchedule eventSchedule = eventScheduleRepository.findByIdWithLock(request.getEventScheduleId())
                .orElseThrow(() -> new RuntimeException("event schedule not found"));

        if (eventSchedule.getEvent().getStatus() != EventStatus.OPEN) {
            throw new RuntimeException("closed event");
        }

        if (LocalDateTime.now().isAfter(eventSchedule.getRegistrationDeadLine())) {
            throw new RuntimeException("event over deadline");
        }

        if (reservationRepository.existsByEventScheduleIdAndPhoneNumber(eventSchedule.getId(), request.getPhoneNumber())) {
            throw new RuntimeException("cannot make another reservation by same user");
        }


        Integer currentCount = reservationRepository.sumTicketsByEventScheduleId(eventSchedule.getId());
        if (currentCount + request.getTicketCount() > eventSchedule.getEvent().getCapacityLimit()) {
            throw new RuntimeException("No reservation left");
        }


        Reservation reservation = Reservation.builder()
                .eventSchedule(eventSchedule)
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .ticketCount(request.getTicketCount())
                .build();

        reservationRepository.save(reservation);

        return createReservationResponse(reservation);
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservation(String phoneNumber) {
        List<Reservation> reservations = reservationRepository.findByPhoneNumberAndEventOpen(phoneNumber);
        List<ReservationResponse> reservationResponses = new ArrayList<>();

        for (Reservation reservation : reservations) {
            reservationResponses.add(createReservationResponse(reservation));
        }

        return reservationResponses;
    }

    @Transactional
    public void deleteReservation(Long eventScheduleId, String phoneNumber) {
        Reservation reservation = reservationRepository.findByEventScheduleIdAndPhoneNumber(eventScheduleId, phoneNumber)
                .orElseThrow(() -> new RuntimeException("no reservation found"));

        reservationRepository.deleteById(reservation.getId());
    }



    //Helper Method ///////////////////////////////////////////////////////////////////
    private ReservationResponse createReservationResponse (Reservation reservation) {
        return ReservationResponse.builder()
                .id(reservation.getId())
                .eventId(reservation.getEventSchedule().getEvent().getId())
                .eventScheduleId(reservation.getEventSchedule().getId())
                .eventName(reservation.getEventSchedule().getEvent().getTitle())
                .name(reservation.getName())
                .ticketCount(reservation.getTicketCount())
                .phoneNumber((reservation.getPhoneNumber()))
                .reservedAt(reservation.getReservedAt())
                .eventDate(reservation.getEventSchedule().getEventDate())
                .build();
    }

}
