package com.judtih.judith_management_system.domain.board.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Tracks when a user last opened a folder — powers the per-folder unread dot.
 *
 * Rule: a folder shows "unread" for a user when its most recent post's createdAt
 * is AFTER this row's lastViewedAt (or when no row exists yet).
 * Opening the folder upserts lastViewedAt to now.
 *
 * Unique on (userId, folder) — one row per user per folder.
 */
@Entity
@Getter
@NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "folder_id"}))
public class FolderReadStatus {

    @Builder
    public FolderReadStatus(Long userId, BoardFolder folder) {
        this.userId = userId;
        this.folder = folder;
        this.lastViewedAt = LocalDateTime.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id", nullable = false)
    private BoardFolder folder;

    @Column(nullable = false)
    private LocalDateTime lastViewedAt;

    /** Called every time the user opens the folder. */
    public void touch() {
        this.lastViewedAt = LocalDateTime.now();
    }
}
