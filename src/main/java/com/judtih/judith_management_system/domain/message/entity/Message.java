package com.judtih.judith_management_system.domain.message.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@RequiredArgsConstructor
@Getter
@Setter
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String messageContent;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL)
    private List<MessageFailure> failures = new ArrayList<>();

    @Column
    private int totalSent;

    @Column
    private int failedAttempt;

    @Column
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

}
