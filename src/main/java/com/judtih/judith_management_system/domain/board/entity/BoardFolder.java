package com.judtih.judith_management_system.domain.board.entity;

import com.judtih.judith_management_system.domain.board.enums.Department;
import com.judtih.judith_management_system.domain.season.Season;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Virtual folder for organising posts within one season+department board.
 *
 * IMPORTANT: this is metadata only. Moving a post between folders changes the
 * post's folder FK — the S3 object key NEVER changes. Deleting a folder moves
 * its posts to root (folder=null), it does not delete them.
 *
 * Flat structure by design — no parent folder. Nesting can be added later as a
 * purely additive column if ever needed.
 */
@Entity
@Getter
@NoArgsConstructor
public class BoardFolder {

    @Builder
    public BoardFolder(Season season, Department department, String name, Long createdByUserId) {
        this.season = season;
        this.department = department;
        this.name = name;
        this.createdByUserId = createdByUserId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "season_id", nullable = false)
    private Season season;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Department department;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long createdByUserId;

    private LocalDateTime createdAt;

    @PrePersist
    private void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public void rename(String name) {
        this.name = name;
    }
}
