package com.judtih.judith_management_system.domain.reservation.service;

import com.judtih.judith_management_system.domain.reservation.entity.Event;
import com.judtih.judith_management_system.domain.reservation.entity.EventSchedule;
import com.judtih.judith_management_system.domain.reservation.eventDto.*;
import com.judtih.judith_management_system.domain.reservation.repository.EventRepository;
import com.judtih.judith_management_system.domain.reservation.repository.EventScheduleRepository;
import com.judtih.judith_management_system.domain.reservation.repository.ReservationRepository;
import com.judtih.judith_management_system.global.storage.StorageFolder;
import com.judtih.judith_management_system.global.storage.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Manages CRUD for Events and EventSchedules, and computes live remaining-seat counts from reservations. */
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventScheduleRepository scheduleRepository;
    private final ReservationRepository reservationRepository;
    private final StorageService storageService;


    //Admin methods ///////////////////////////////////////////////////////

    @Transactional
    public EventResponse createEvent(EventRequest eventRequest) {
        Event event = Event.builder()
                .title(eventRequest.getTitle())
                .description(eventRequest.getDescription())
                .location(eventRequest.getLocation())
                .capacityLimit(eventRequest.getCapacityLimit())
                .status(eventRequest.getStatus())
                .posterImageUrl(eventRequest.getPosterImageUrl())
                .build();

        eventRepository.save(event);

        return createEventResponse(event);

    }

    @Transactional
    public EventScheduleResponse createEventSchedule (EventScheduleRequest request) {
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new RuntimeException("event not found"));

        EventSchedule eventSchedule = EventSchedule.builder()
                .eventDate(request.getEventDate())
                .registrationDeadLine(request.getRegistrationDeadLine())
                .event(event)
                .build();

        scheduleRepository.save(eventSchedule);

        return createEventScheduleResponse(eventSchedule, event.getCapacityLimit());
    }


    @Transactional
    public EventResponse updateEvent(Long id, EventRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        event.eventUpdate(request.getTitle(),request.getDescription()
                ,request.getLocation(),request.getCapacityLimit()
                ,request.getStatus(),request.getPosterImageUrl());


        return createEventResponse(event);
    }

    @Transactional
    public EventScheduleResponse updateSchedule(Long id, EventScheduleRequest request) {
        EventSchedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        schedule.updateEventSchedule(request.getEventDate(),
                request.getRegistrationDeadLine());

        return createEventScheduleResponse(schedule, schedule.getEvent().getCapacityLimit());

    }


    @Transactional
    public void deleteEventById(Long id) {
        eventRepository.deleteById(id);

    }

    @Transactional
    public void deleteScheduleById(Long id) {
        scheduleRepository.deleteById(id);
    }

    //User method /////////////////////////////////////////////////////////

    @Transactional(readOnly = true)
    public EventResponse getLatestEvent() {
        Optional<Event> event = eventRepository.findTopByStatusOrderByCreatedAtDesc(com.judtih.judith_management_system.domain.reservation.entity.EventStatus.OPEN);
        if (event.isEmpty()) {
            event = eventRepository.findTopByOrderByCreatedAtDesc();
        }
        return event.map(e -> {
            List<EventSchedule> schedules = scheduleRepository.findByEventId(e.getId());
            List<EventScheduleResponse> scheduleResponses = new ArrayList<>();
            for (EventSchedule s : schedules) {
                scheduleResponses.add(createEventScheduleResponse(s, e.getCapacityLimit()));
            }
            return createEventResponse(e, scheduleResponses);
        }).orElseThrow(() -> new RuntimeException("No events found"));
    }

    @Transactional(readOnly = true)
    public EventResponse getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("event not found"));

        List<EventSchedule> schedules = scheduleRepository.findByEventId(id);
        List<EventScheduleResponse> scheduleResponses = new ArrayList<>();

        for(EventSchedule schedule : schedules) {
            scheduleResponses.add(createEventScheduleResponse(schedule, event.getCapacityLimit()));
        }


        return createEventResponse(event, scheduleResponses);
    }


    @Transactional(readOnly = true)
    public List<EventListResponse> getAllEvent() {
        List<Event> events = eventRepository.findAll();
        List<EventListResponse> responseList = new ArrayList<>();

        for(Event event : events) {
            EventListResponse response = createEventListResponse(event);

            responseList.add(response);
        }

        return responseList;
    }

    @Transactional(readOnly = true)
    public String getPamphletUrl(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        if (event.getPamphletUrl() == null) {
            throw new RuntimeException("No pamphlet found");
        }
        return event.getPamphletUrl();
    }

    @Transactional
    public EventResponse uploadPamphlet(Long eventId, MultipartFile file, Long seasonId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        String url = storageService.uploadFile(file, StorageFolder.PAMPHLET, seasonId).getUrl();
        event.updatePamphletUrl(url);
        return createEventResponse(event);
    }

    //Helper method ///////////////////////////////////////////////////////

    //create method without List<scheduleResponse>
    private EventResponse createEventResponse(Event event) {

        return EventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .location(event.getLocation())
                .capacityLimit(event.getCapacityLimit())
                .status(event.getStatus())
                .posterImageUrl((event.getPosterImageUrl()))
                .pamphletUrl(event.getPamphletUrl())
                .createdAt(event.getCreatedAt())
                .build();
    }

    //create method that have List<scheduleResponse>
    private EventResponse createEventResponse(Event event, List<EventScheduleResponse> scheduleResponses) {
        return EventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .location(event.getLocation())
                .capacityLimit(event.getCapacityLimit())
                .status(event.getStatus())
                .posterImageUrl((event.getPosterImageUrl()))
                .pamphletUrl(event.getPamphletUrl())
                .createdAt(event.getCreatedAt())
                .schedules(scheduleResponses)
                .build();
    }

    private EventScheduleResponse createEventScheduleResponse(EventSchedule schedule, Integer capacityLimit) {

        Integer bookedTickets = reservationRepository.sumTicketsByEventScheduleId(schedule.getId());
        Integer remainingSeats = capacityLimit - bookedTickets;

        return EventScheduleResponse.builder()
                .eventScheduleId(schedule.getId())
                .eventId(schedule.getEvent().getId())
                .eventDate(schedule.getEventDate())
                .registrationDeadLine(schedule.getRegistrationDeadLine())
                .remainingSeats(remainingSeats)
                .build();
    }

    private EventListResponse createEventListResponse(Event event) {
        return EventListResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .posterImageUrl(event.getPosterImageUrl())
                .status(event.getStatus())
                .capacityLimit(event.getCapacityLimit())
                .build();
    }
}
