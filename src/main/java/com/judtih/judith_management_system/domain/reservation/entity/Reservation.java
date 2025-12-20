package com.judtih.judith_management_system.domain.reservation.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"event_schedule_id", "phone_number"}))
@Getter
@NoArgsConstructor
public class Reservation {

    @Builder
    public Reservation(EventSchedule eventSchedule, String name, String phoneNumber, Integer ticketCount) {
        this.eventSchedule = eventSchedule;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.ticketCount = ticketCount;
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_schedule_id", nullable = false)
    EventSchedule eventSchedule;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private Integer ticketCount;

    private LocalDateTime reservedAt;

    @PrePersist
    protected void onCreate() {
        reservedAt = LocalDateTime.now();
    }
}
