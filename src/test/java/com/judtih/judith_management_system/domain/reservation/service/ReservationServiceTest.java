package com.judtih.judith_management_system.domain.reservation.service;

import com.judtih.judith_management_system.domain.reservation.entity.Event;
import com.judtih.judith_management_system.domain.reservation.entity.EventSchedule;
import com.judtih.judith_management_system.domain.reservation.entity.EventStatus;
import com.judtih.judith_management_system.domain.reservation.repository.EventScheduleRepository;
import com.judtih.judith_management_system.domain.reservation.repository.ReservationRepository;
import com.judtih.judith_management_system.domain.reservation.reservationDto.ReservationRequest;
import com.judtih.judith_management_system.domain.reservation.reservationDto.ReservationResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private EventScheduleRepository eventScheduleRepository;

    @InjectMocks
    private ReservationService reservationService;

    private Event openEvent() {
        return Event.builder()
                .title("노부인의 방문")
                .description("test")
                .location("대학로 극장동국")
                .capacityLimit(50)
                .status(EventStatus.OPEN)
                .build();
    }

    private EventSchedule futureSchedule(Event event) {
        return EventSchedule.builder()
                .event(event)
                .eventDate(LocalDateTime.of(2026, 6, 1, 19, 0))
                .registrationDeadLine(LocalDateTime.now().plusDays(7))
                .build();
    }

    private ReservationRequest validRequest() {
        return ReservationRequest.builder()
                .eventScheduleId(1L)
                .name("홍길동")
                .phoneNumber("01012345678")
                .ticketCount(2)
                .build();
    }

    @Test
    void createReservation_shouldSucceed_whenValid() {
        EventSchedule schedule = futureSchedule(openEvent());

        when(eventScheduleRepository.findByIdWithLock(1L)).thenReturn(Optional.of(schedule));
        when(reservationRepository.existsByEventScheduleIdAndPhoneNumber(any(), anyString())).thenReturn(false);
        when(reservationRepository.sumTicketsByEventScheduleId(any())).thenReturn(2);

        ReservationResponse result = reservationService.createReservation(validRequest());

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("홍길동");
        assertThat(result.getTicketCount()).isEqualTo(2);
    }

    @Test
    void createReservation_shouldThrow_whenEventClosed() {
        Event closedEvent = Event.builder()
                .title("노부인의 방문")
                .description("test")
                .location("대학로 극장동국")
                .capacityLimit(50)
                .status(EventStatus.CLOSED)
                .build();

        when(eventScheduleRepository.findByIdWithLock(1L)).thenReturn(Optional.of(futureSchedule(closedEvent)));

        assertThatThrownBy(() -> reservationService.createReservation(validRequest()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("closed event");
    }

    @Test
    void createReservation_shouldThrow_whenDeadlinePassed() {
        EventSchedule expiredSchedule = EventSchedule.builder()
                .event(openEvent())
                .eventDate(LocalDateTime.of(2026, 1, 1, 19, 0))
                .registrationDeadLine(LocalDateTime.now().minusDays(1))
                .build();

        when(eventScheduleRepository.findByIdWithLock(1L)).thenReturn(Optional.of(expiredSchedule));

        assertThatThrownBy(() -> reservationService.createReservation(validRequest()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("deadline");
    }

    @Test
    void createReservation_shouldThrow_whenDuplicatePhoneNumber() {
        when(eventScheduleRepository.findByIdWithLock(1L)).thenReturn(Optional.of(futureSchedule(openEvent())));
        when(reservationRepository.existsByEventScheduleIdAndPhoneNumber(any(), anyString())).thenReturn(true);

        assertThatThrownBy(() -> reservationService.createReservation(validRequest()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("same user");
    }

    @Test
    void createReservation_shouldThrow_whenNoSeatsLeft() {
        when(eventScheduleRepository.findByIdWithLock(1L)).thenReturn(Optional.of(futureSchedule(openEvent())));
        when(reservationRepository.existsByEventScheduleIdAndPhoneNumber(any(), anyString())).thenReturn(false);
        when(reservationRepository.sumTicketsByEventScheduleId(any())).thenReturn(49); // 49 + 2 > 50

        assertThatThrownBy(() -> reservationService.createReservation(validRequest()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No reservation left");
    }
}
