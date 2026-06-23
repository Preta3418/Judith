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

    // One-directional state machine: PREPARING → ACTIVE → CLOSED. There is no reversal path.
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

    // startDate is set to now() at activation — the moment of activation IS the start date, so user input would be wrong
    public void activateSeason () {
        this.status = Status.ACTIVE;
        this.startDate = LocalDate.now();
    }

    // endDate is set to now() at close — same reasoning as startDate in activateSeason()
    public void closeSeason () {
        this.status = Status.CLOSED;
        this.endDate = LocalDate.now();
    }

    public void reopenSeason() {
        this.status = Status.ACTIVE;
        this.endDate = null;
    }



}
