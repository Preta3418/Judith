package com.judtih.judith_management_system.domain.season;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Season {

    @Builder
    public Season(String name, LocalDate startDate) {
        this.name = name;
        this.startDate = startDate;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    @Enumerated(EnumType.STRING)
    private Status status =  Status.PREPARING;

    @Column
    private LocalDate startDate;

    @Column(nullable = true)
    private LocalDate endDate;

    @Column(nullable = true)
    private LocalDate eventDate;

    @Column
    private LocalDateTime createdAt;

    @PrePersist
    private void onCreate() {
        createdAt = LocalDateTime.now();
    }


    public void updateSeason (String name, LocalDate startDate, LocalDate eventDate) {
        if(name != null) this.name = name;
        if(startDate != null) this.startDate = startDate;
        if(eventDate != null) this.eventDate = eventDate;
    }

    public void activateSeason () {
        this.status = Status.ACTIVE;
    }

    public void closeSeason () {
        this.status = Status.CLOSED;
        this.endDate = LocalDate.now();
    }



}
