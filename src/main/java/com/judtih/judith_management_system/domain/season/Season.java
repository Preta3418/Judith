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

    public Season(String name) {
        this.name = name;
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


    public void updateSeason (String name, LocalDate eventDate) {
        if(name != null) this.name = name;
        if(eventDate != null) this.eventDate = eventDate;
    }

    public void activateSeason () {
        this.status = Status.ACTIVE;
        this.startDate = LocalDate.now();
    }

    public void closeSeason () {
        this.status = Status.CLOSED;
        this.endDate = LocalDate.now();
    }



}
