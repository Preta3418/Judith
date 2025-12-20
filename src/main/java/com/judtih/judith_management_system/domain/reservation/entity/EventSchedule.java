package com.judtih.judith_management_system.domain.reservation.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class EventSchedule {

    @Builder
    public EventSchedule(Event event, LocalDateTime eventDate, LocalDateTime registrationDeadLine) {
        this.event = event;
        this.eventDate = eventDate;
        this.registrationDeadLine = registrationDeadLine;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column
    private LocalDateTime eventDate;

    @Column
    private LocalDateTime registrationDeadLine;

    @Column
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now();}


    public void updateEventSchedule (LocalDateTime eventDate, LocalDateTime registrationDeadLine) {
        if (eventDate != null) this.eventDate = eventDate;
        if (registrationDeadLine != null) this.registrationDeadLine = registrationDeadLine;
    }

}
