package com.judtih.judith_management_system.domain.message.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@RequiredArgsConstructor
@Getter
@Setter
public class MessageFailure {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="message_id", nullable = false)
    private Message message;

    @Column
    private Long userId;

    @Column
    private String userName;

    @Column
    private String phoneNumber;

    @Column
    private String errorMessage;
}
