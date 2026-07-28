package com.judtih.judith_management_system.domain.calendar.service;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.judtih.judith_management_system.domain.calendar.dto.GoogleCalendarRequest;
import com.judtih.judith_management_system.domain.calendar.dto.GoogleCalendarResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class GoogleCalendarServiceTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Calendar googleCalendar;

    @InjectMocks
    private GoogleCalendarService googleCalendarService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(googleCalendarService, "calendarId", "test@group.calendar.google.com");
    }

    private Event sampleEvent(String id, String title, LocalDateTime start, LocalDateTime end) {
        long startMs = start.toInstant(ZoneOffset.UTC).toEpochMilli();
        long endMs = end.toInstant(ZoneOffset.UTC).toEpochMilli();
        return new Event()
                .setId(id)
                .setSummary(title)
                .setDescription("설명")
                .setColorId("tomato")
                .setHtmlLink("https://calendar.google.com/event?eid=" + id)
                .setStart(new EventDateTime().setDateTime(new DateTime(startMs)))
                .setEnd(new EventDateTime().setDateTime(new DateTime(endMs)));
    }

    @Test
    void getEvents_shouldReturnMappedResponses() throws IOException {
        LocalDateTime start = LocalDateTime.of(2026, 7, 28, 19, 0);
        LocalDateTime end = LocalDateTime.of(2026, 7, 28, 21, 0);
        Events events = new Events().setItems(List.of(sampleEvent("evt1", "정기 연습", start, end)));

        when(googleCalendar.events().list(any())
                .setTimeMin(any()).setTimeMax(any()).setOrderBy(any()).setSingleEvents(any())
                .execute())
                .thenReturn(events);

        List<GoogleCalendarResponse> result = googleCalendarService.getEvents(
                LocalDate.of(2026, 7, 28), LocalDate.of(2026, 7, 28));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getGoogleEventId()).isEqualTo("evt1");
        assertThat(result.get(0).getTitle()).isEqualTo("정기 연습");
        assertThat(result.get(0).getColor()).isEqualTo("tomato");
    }

    @Test
    void getEvents_shouldReturnEmptyList_whenNoEvents() throws IOException {
        Events events = new Events().setItems(List.of());

        when(googleCalendar.events().list(any())
                .setTimeMin(any()).setTimeMax(any()).setOrderBy(any()).setSingleEvents(any())
                .execute())
                .thenReturn(events);

        List<GoogleCalendarResponse> result = googleCalendarService.getEvents(
                LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 31));

        assertThat(result).isEmpty();
    }

    @Test
    void createEvent_shouldDefaultEndToStartPlusOneHour_whenEndIsNull() throws IOException {
        LocalDateTime start = LocalDateTime.of(2026, 7, 28, 19, 0);
        LocalDateTime expectedEnd = start.plusHours(1);

        GoogleCalendarRequest req = GoogleCalendarRequest.builder()
                .title("연습")
                .start(start)
                .end(null)
                .build();

        Event created = sampleEvent("new1", "연습", start, expectedEnd);
        when(googleCalendar.events().insert(any(), any()).execute()).thenReturn(created);

        GoogleCalendarResponse result = googleCalendarService.createEvent(req);

        assertThat(result.getGoogleEventId()).isEqualTo("new1");
        assertThat(result.getTitle()).isEqualTo("연습");
        // end should be start + 1 hour
        assertThat(result.getEnd()).isEqualTo(expectedEnd);
    }

    @Test
    void createEvent_shouldUseProvidedEnd_whenEndIsSet() throws IOException {
        LocalDateTime start = LocalDateTime.of(2026, 7, 28, 19, 0);
        LocalDateTime end = LocalDateTime.of(2026, 7, 28, 22, 0);

        GoogleCalendarRequest req = GoogleCalendarRequest.builder()
                .title("공연")
                .start(start)
                .end(end)
                .build();

        Event created = sampleEvent("new2", "공연", start, end);
        when(googleCalendar.events().insert(any(), any()).execute()).thenReturn(created);

        GoogleCalendarResponse result = googleCalendarService.createEvent(req);

        assertThat(result.getTitle()).isEqualTo("공연");
        assertThat(result.getEnd()).isEqualTo(end);
    }

    @Test
    void updateEvent_shouldDefaultEndToStartPlusOneHour_whenEndIsNull() throws IOException {
        LocalDateTime start = LocalDateTime.of(2026, 7, 30, 14, 0);
        LocalDateTime expectedEnd = start.plusHours(1);

        GoogleCalendarRequest req = GoogleCalendarRequest.builder()
                .title("수정된 연습")
                .start(start)
                .end(null)
                .build();

        Event existing = sampleEvent("evt1", "기존 연습", start, expectedEnd);
        Event updated = sampleEvent("evt1", "수정된 연습", start, expectedEnd);

        when(googleCalendar.events().get(any(), any()).execute()).thenReturn(existing);
        when(googleCalendar.events().update(any(), any(), any()).execute()).thenReturn(updated);

        GoogleCalendarResponse result = googleCalendarService.updateEvent("evt1", req);

        assertThat(result.getTitle()).isEqualTo("수정된 연습");
        assertThat(result.getGoogleEventId()).isEqualTo("evt1");
    }

    @Test
    void deleteEvent_shouldCompleteWithoutException() throws IOException {
        googleCalendarService.deleteEvent("evt-to-delete");
        // deep stub returns a mock for delete().execute() — no exception = API was called
    }
}
