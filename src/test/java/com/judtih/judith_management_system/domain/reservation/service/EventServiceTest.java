package com.judtih.judith_management_system.domain.reservation.service;

import com.judtih.judith_management_system.domain.reservation.entity.Event;
import com.judtih.judith_management_system.domain.reservation.entity.EventSchedule;
import com.judtih.judith_management_system.domain.reservation.entity.EventStatus;
import com.judtih.judith_management_system.domain.reservation.eventDto.EventRequest;
import com.judtih.judith_management_system.domain.reservation.eventDto.EventResponse;
import com.judtih.judith_management_system.domain.reservation.eventDto.EventScheduleRequest;
import com.judtih.judith_management_system.domain.reservation.eventDto.EventScheduleResponse;
import com.judtih.judith_management_system.domain.reservation.repository.EventRepository;
import com.judtih.judith_management_system.domain.reservation.repository.EventScheduleRepository;
import com.judtih.judith_management_system.domain.reservation.repository.ReservationRepository;
import com.judtih.judith_management_system.global.storage.StorageFolder;
import com.judtih.judith_management_system.global.storage.dto.StoredFileResponse;
import com.judtih.judith_management_system.global.storage.service.StorageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventScheduleRepository scheduleRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private StorageService storageService;

    @Mock
    private MultipartFile file;

    @InjectMocks
    private EventService eventService;

    private Event openEvent() {
        return Event.builder()
                .title("노부인의 방문")
                .description("desc")
                .location("대학로")
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

    @Test
    void createEvent_shouldReturnResponse_whenValid() {
        Event event = openEvent();
        EventRequest request = EventRequest.builder()
                .title("노부인의 방문")
                .description("desc")
                .location("대학로")
                .capacityLimit(50)
                .status(EventStatus.OPEN)
                .build();

        when(eventRepository.save(any(Event.class))).thenReturn(event);

        EventResponse result = eventService.createEvent(request);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("노부인의 방문");
    }

    @Test
    void createEventSchedule_shouldThrow_whenEventNotFound() {
        EventScheduleRequest request = EventScheduleRequest.builder()
                .eventId(99L)
                .eventDate(LocalDateTime.of(2026, 6, 1, 19, 0))
                .registrationDeadLine(LocalDateTime.now().plusDays(7))
                .build();

        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.createEventSchedule(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("event not found");
    }

    @Test
    void updateEvent_shouldThrow_whenEventNotFound() {
        EventRequest request = EventRequest.builder()
                .title("새 제목")
                .description("desc")
                .location("대학로")
                .capacityLimit(50)
                .status(EventStatus.OPEN)
                .build();

        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.updateEvent(99L, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Event not found");
    }

    @Test
    void deleteEventById_shouldCallRepository() {
        doNothing().when(eventRepository).deleteById(1L);

        eventService.deleteEventById(1L);

        verify(eventRepository).deleteById(1L);
    }

    @Test
    void getEventById_shouldReturnResponse_whenFound() {
        Event event = openEvent();
        EventSchedule schedule = futureSchedule(event);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(scheduleRepository.findByEventId(1L)).thenReturn(List.of(schedule));
        when(reservationRepository.sumTicketsByEventScheduleId(any())).thenReturn(10);

        EventResponse result = eventService.getEventById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("노부인의 방문");
        assertThat(result.getSchedules()).hasSize(1);
    }

    @Test
    void getEventById_shouldThrow_whenNotFound() {
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.getEventById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("event not found");
    }

    @Test
    void getLatestEvent_shouldPreferOpenEvent() {
        Event openEvent = openEvent();
        EventSchedule schedule = futureSchedule(openEvent);

        when(eventRepository.findTopByStatusOrderByCreatedAtDesc(EventStatus.OPEN))
                .thenReturn(Optional.of(openEvent));
        when(scheduleRepository.findByEventId(any())).thenReturn(List.of(schedule));
        when(reservationRepository.sumTicketsByEventScheduleId(any())).thenReturn(5);

        EventResponse result = eventService.getLatestEvent();

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("노부인의 방문");
        verify(eventRepository, never()).findTopByOrderByCreatedAtDesc();
    }

    @Test
    void getLatestEvent_shouldFallbackToAny_whenNoOpenEvent() {
        Event closedEvent = Event.builder()
                .title("노부인의 방문")
                .description("desc")
                .location("대학로")
                .capacityLimit(50)
                .status(EventStatus.CLOSED)
                .build();

        when(eventRepository.findTopByStatusOrderByCreatedAtDesc(EventStatus.OPEN))
                .thenReturn(Optional.empty());
        when(eventRepository.findTopByOrderByCreatedAtDesc())
                .thenReturn(Optional.of(closedEvent));
        when(scheduleRepository.findByEventId(any())).thenReturn(List.of());

        EventResponse result = eventService.getLatestEvent();

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("노부인의 방문");
        verify(eventRepository).findTopByOrderByCreatedAtDesc();
    }

    @Test
    void getLatestEvent_shouldThrow_whenNoEvents() {
        when(eventRepository.findTopByStatusOrderByCreatedAtDesc(EventStatus.OPEN))
                .thenReturn(Optional.empty());
        when(eventRepository.findTopByOrderByCreatedAtDesc())
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.getLatestEvent())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No events found");
    }

    @Test
    void uploadPamphlet_shouldSetUrl_whenFileUploaded() {
        Event event = openEvent();
        StoredFileResponse storedFileResponse = StoredFileResponse.builder()
                .url("https://example.com/pamphlet.pdf")
                .build();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(storageService.uploadFile(file, StorageFolder.PAMPHLET, 1L)).thenReturn(storedFileResponse);

        EventResponse result = eventService.uploadPamphlet(1L, file, 1L);

        assertThat(result).isNotNull();
        assertThat(result.getPamphletUrl()).isEqualTo("https://example.com/pamphlet.pdf");
    }
}
