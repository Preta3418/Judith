package com.judtih.judith_management_system.domain.calendar.service;

import com.google.api.client.util.DateTime;
import org.springframework.beans.factory.annotation.Value;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.judtih.judith_management_system.domain.calendar.dto.GoogleCalendarRequest;
import com.judtih.judith_management_system.domain.calendar.dto.GoogleCalendarResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Wraps Google Calendar API calls using a Service Account.
 * Google Calendar is the source of truth — no events are stored in our DB.
 * Admin creates/edits/deletes via our dashboard; changes sync to Google Calendar in real time.
 */
@Service
@RequiredArgsConstructor
public class GoogleCalendarService {

    private final Calendar googleCalendar;

    @Value("${google.calendar.id}")
    private String calendarId;

    // Fetches events in the given date range from the club's Google Calendar.
    public List<GoogleCalendarResponse> getEvents(LocalDate from, LocalDate to) throws IOException {
        Events events = googleCalendar.events().list(calendarId)
                .setTimeMin(toGoogleDateTime(from.atStartOfDay()))
                .setTimeMax(toGoogleDateTime(to.plusDays(1).atStartOfDay()))
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        return events.getItems().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Creates a new event. If no end time is provided, defaults to start + 1 hour.
    public GoogleCalendarResponse createEvent(GoogleCalendarRequest req) throws IOException {
        LocalDateTime end = req.getEnd() != null ? req.getEnd() : req.getStart().plusHours(1);

        Event event = new Event()
                .setSummary(req.getTitle())
                .setDescription(req.getDescription())
                .setColorId(req.getColor());

        EventDateTime start = new EventDateTime()
                .setDateTime(toGoogleDateTime(req.getStart()));
        EventDateTime endTime = new EventDateTime()
                .setDateTime(toGoogleDateTime(end));

        event.setStart(start);
        event.setEnd(endTime);

        Event created = googleCalendar.events().insert(calendarId, event).execute();
        return toResponse(created);
    }


    // Fetches the existing event from Google, applies changes, and pushes the update back.
    public GoogleCalendarResponse updateEvent(String googleEventId, GoogleCalendarRequest req) throws IOException {
        LocalDateTime end = req.getEnd() != null ? req.getEnd() : req.getStart().plusHours(1);

        Event event = googleCalendar.events().get(calendarId, googleEventId).execute();

        event.setSummary(req.getTitle())
                .setDescription(req.getDescription())
                .setColorId(req.getColor());

        EventDateTime start = new EventDateTime()
                .setDateTime(toGoogleDateTime(req.getStart()));
        EventDateTime endTime = new EventDateTime()
                .setDateTime(toGoogleDateTime(end));

        event.setStart(start);
        event.setEnd(endTime);

        Event updated = googleCalendar.events().update(calendarId, googleEventId, event).execute();
        return toResponse(updated);
    }

    // Deletes an event from Google Calendar by its Google-assigned event ID.
    public void deleteEvent(String googleEventId) throws IOException {
        googleCalendar.events().delete(calendarId, googleEventId).execute();
    }

    ///////////////////////////////////////////////////////////////////// helper method

    //turn LocalDateTime to google's DateTime
    private DateTime toGoogleDateTime(LocalDateTime ldt) {
        return new DateTime(ldt.toInstant(ZoneOffset.UTC).toEpochMilli());
    }

    private GoogleCalendarResponse toResponse(Event event) {
        return GoogleCalendarResponse.builder()
                .googleEventId(event.getId())
                .title(event.getSummary())
                .description(event.getDescription())
                .start(toLocalDateTime(event.getStart()))
                .end(toLocalDateTime(event.getEnd()))
                .color(event.getColorId())
                .htmlLink(event.getHtmlLink())
                .build();
    }

    private LocalDateTime toLocalDateTime(EventDateTime edt) {
        if (edt.getDateTime() != null) {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(edt.getDateTime().getValue()), ZoneOffset.UTC);
        }
        // all-day event — getDate() returns a date-only DateTime
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(edt.getDate().getValue()), ZoneOffset.UTC);
    }
}
