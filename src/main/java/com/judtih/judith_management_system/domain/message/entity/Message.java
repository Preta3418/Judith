package com.judtih.judith_management_system.domain.message.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    public void updateMessage (String messageContent, Integer totalSent, Integer failedAttempt, List<MessageFailure> failures) {
        if (messageContent != null) this.messageContent = messageContent ;
        if (totalSent != null) this.totalSent = totalSent;
        if (failedAttempt != null) this.failedAttempt = failedAttempt;
        if (failures != null) this.failures = failures;
    }

}
