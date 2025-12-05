package com.judtih.judith_management_system.domain.reservation.service;

import com.judtih.judith_management_system.domain.reservation.entity.Event;
import com.judtih.judith_management_system.domain.reservation.eventDto.EventListResponse;
import com.judtih.judith_management_system.domain.reservation.eventDto.EventRequest;
import com.judtih.judith_management_system.domain.reservation.eventDto.EventResponse;
import com.judtih.judith_management_system.domain.reservation.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;


    //Admin methods ///////////////////////////////////////////////////////

    @Transactional
    public EventResponse createEvent(EventRequest eventRequest) {
        Event event = Event.builder()
                .title(eventRequest.getTitle())
                .eventDate(eventRequest.getEventDate())
                .description(eventRequest.getDescription())
                .location(eventRequest.getLocation())
                .capacityLimit(eventRequest.getCapacityLimit())
                .registrationDeadline(eventRequest.getRegistrationDeadLine())
                .status(eventRequest.getStatus())
                .posterImageUrl(eventRequest.getPosterImageUrl())
                .build();

        eventRepository.save(event);

        return createEventResponse(event);

    }


    @Transactional
    public EventResponse updateEvent(Long id, EventRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        event.eventUpdate(request.getTitle(),request.getDescription()
                ,request.getEventDate(),request.getLocation(),request.getCapacityLimit()
                ,request.getRegistrationDeadLine(),request.getStatus(),request.getPosterImageUrl());


        return createEventResponse(event);
    }


    @Transactional
    public void deleteEventById(Long id) {
        eventRepository.deleteById(id);

    }

    //User method /////////////////////////////////////////////////////////

    @Transactional(readOnly = true)
    public EventResponse getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("event not found"));

        return createEventResponse(event);
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

    private EventResponse createEventResponse(Event event) {

        return EventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .location(event.getLocation())
                .capacityLimit(event.getCapacityLimit())
                .registrationDeadLine(event.getRegistrationDeadline())
                .status(event.getStatus())
                .posterImageUrl((event.getPosterImageUrl()))
                .createdAt(event.getCreatedAt())
                .build();
    }

    private EventListResponse createEventListResponse(Event event) {
        return EventListResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .eventDate(event.getEventDate())
                .posterImageUrl(event.getPosterImageUrl())
                .status(event.getStatus())
                .build();
    }
}
