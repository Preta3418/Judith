package com.judtih.judith_management_system.domain.announcement.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** A season-scoped announcement created by full-access members and delivered to all season members. */
@Entity
@Getter
@NoArgsConstructor
public class Announcement {

    @Builder
    public Announcement(String title, String content, Long seasonId) {
        this.title = title;
        this.content = content;
        this.seasonId = seasonId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Long seasonId;

    private boolean isPinned = false;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() { updatedAt = LocalDateTime.now(); }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void pin()   { this.isPinned = true; }
    public void unpin() { this.isPinned = false; }
}
