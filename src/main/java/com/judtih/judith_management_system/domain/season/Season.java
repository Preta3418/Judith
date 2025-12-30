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
    LocalDate startDate;

    @Column(nullable = true)
    LocalDate endDate;

    @Column(nullable = true)
    LocalDate eventDate;

    @Column
    LocalDateTime createdAt;

    @PrePersist
    private void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public void updateSeason (String name, Status status, LocalDate startDate, LocalDate endDate) {
        if(name != null) this.name = name;
        if(status != null) this.status = status;
        if(startDate != null) this.startDate = startDate;
        if(endDate != null) this.endDate = endDate;
    }

}
