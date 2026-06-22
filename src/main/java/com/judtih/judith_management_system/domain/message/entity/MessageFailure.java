package com.judtih.judith_management_system.domain.message.entity;

import jakarta.persistence.*;
import lombok.*;

/** Stores a single failed SNS delivery attempt associated with a Message broadcast. */
@Entity
@NoArgsConstructor
@Getter
public class MessageFailure {

    @Builder
    public MessageFailure(Long userId, String userName, String phoneNumber, String errorMessage, Message message) {
        this.userId = userId;
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.errorMessage = errorMessage;
        this.message = message;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long userId;

    @Column
    private String userName;

    @Column
    private String phoneNumber;

    @Column
    private String errorMessage; // SNS awsErrorDetails message, or RuntimeException message for non-SNS failures

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="message_id", nullable = false)
    private Message message;

}
