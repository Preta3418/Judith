package com.judtih.judith_management_system.domain.reservation.service;

import com.judtih.judith_management_system.domain.reservation.entity.Event;
import com.judtih.judith_management_system.domain.reservation.entity.EventSchedule;
import com.judtih.judith_management_system.domain.reservation.eventDto.*;
import com.judtih.judith_management_system.domain.reservation.repository.EventRepository;
import com.judtih.judith_management_system.domain.reservation.repository.EventScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventScheduleRepository scheduleRepository;


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

        return createEventScheduleResponse(eventSchedule);
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

        return createEventScheduleResponse(schedule);

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
    public EventResponse getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("event not found"));

        List<EventSchedule> schedules = scheduleRepository.findByEventId(id);
        List<EventScheduleResponse> scheduleResponses = new ArrayList<>();

        for(EventSchedule schedule : schedules) {
            scheduleResponses.add(createEventScheduleResponse(schedule));
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
                .createdAt(event.getCreatedAt())
                .schedules(scheduleResponses)
                .build();
    }

    private EventScheduleResponse createEventScheduleResponse(EventSchedule schedule) {
        return EventScheduleResponse.builder()
                .eventScheduleId(schedule.getId())
                .eventId(schedule.getEvent().getId())
                .eventDate(schedule.getEventDate())
                .registrationDeadLine(schedule.getRegistrationDeadLine())
                .build();
    }

    private EventListResponse createEventListResponse(Event event) {
        return EventListResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .posterImageUrl(event.getPosterImageUrl())
                .status(event.getStatus())
                .build();
    }
}
