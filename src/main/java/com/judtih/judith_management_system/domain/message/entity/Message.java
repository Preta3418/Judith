package com.judtih.judith_management_system.domain.message.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Audit record of a bulk SMS broadcast, including per-recipient failure details. */
@Entity
@NoArgsConstructor
@Getter
public class Message {

    @Builder
    public Message(String messageContent) {
        this.messageContent = messageContent;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String messageContent;

    @Column
    private int totalSent;

    @Column
    private int failedAttempt;

    @Column
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL)
    private List<MessageFailure> failures = new ArrayList<>();


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /** Populates delivery stats after the SNS send loop; null arguments are ignored (no-op for that field). */
    public void updateMessage (String messageContent, Integer totalSent, Integer failedAttempt, List<MessageFailure> failures) {
        if (messageContent != null) this.messageContent = messageContent ;
        if (totalSent != null) this.totalSent = totalSent;
        if (failedAttempt != null) this.failedAttempt = failedAttempt;
        if (failures != null) this.failures = failures;
    }

}
