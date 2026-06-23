package com.judtih.judith_management_system.domain.calendar.entity;

import com.judtih.judith_management_system.domain.calendar.enums.EventColor;
import com.judtih.judith_management_system.domain.calendar.enums.EventScope;
import com.judtih.judith_management_system.domain.reservation.entity.Event;
import com.judtih.judith_management_system.domain.season.Season;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class CalendarEvent {

    @Builder
    public CalendarEvent(Season season, String title, String description, LocalDateTime eventDateTime, EventScope eventScope, EventColor eventColor, Long createdByUserID) {
        this.season = season;
        this.title = title;
        this.description = description;
        this.eventDateTime = eventDateTime;
        this.eventScope = eventScope;
        this.eventColor = eventColor;
        this.createdByUserID = createdByUserID;
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "season_id")
    private Season season;

    @Column (nullable = false)
    private String title;

    @Column
    private String description;

    @Column (nullable = false)
    private LocalDateTime eventDateTime;

    @Column (nullable = false)
    @Enumerated(EnumType.STRING)
    private EventScope eventScope;

    // For SHARED events, color carries team-wide meaning and should follow a fixed convention.
    // For PERSONAL events, it's purely user preference with no semantic constraint.
    @Column (nullable = false)
    @Enumerated(EnumType.STRING)
    private EventColor eventColor;

    @Column (nullable = false)
    private Long createdByUserID;

    @Column
    private LocalDateTime createdAt;



    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void update(String title, String description, LocalDateTime eventDateTime, EventColor eventColor) {
        this.title = title;
        this.description = description;
        this.eventDateTime = eventDateTime;
        this.eventColor = eventColor;
    }


}
