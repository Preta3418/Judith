package com.judtih.judith_management_system.domain.reservation.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Event {

    @Builder
    public Event(String title, String description, String location, Integer
            capacityLimit, String posterImageUrl, EventStatus status) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.status = status;
        this.capacityLimit = capacityLimit;
        this.posterImageUrl = posterImageUrl;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private Integer capacityLimit;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EventStatus status = EventStatus.CLOSED;

    private String posterImageUrl;

    private LocalDateTime createdAt;


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public void eventUpdate(String title, String description, String location, Integer
            capacityLimit, EventStatus status, String posterImageUrl) {

        if (title != null) this.title = title;
        if (description != null) this.description = description;
        if (location != null) this.location = location;
        if (capacityLimit != null) this.capacityLimit = capacityLimit;
        if (status != null) this.status = status;
        if (posterImageUrl != null) this.posterImageUrl = posterImageUrl;
    }
}
