package com.judtih.judith_management_system.domain.board.entity;

import com.judtih.judith_management_system.domain.board.enums.Department;
import com.judtih.judith_management_system.domain.season.Season;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * A board post — a pure container: title, body text, folder placement.
 * All file/URL/audio data lives in {@link PostAttachment} child rows, of which
 * there can be many per post.
 *
 * Attachments are IMMUTABLE after creation — update() only touches title/content.
 * To change attachments, delete and re-create the post.
 */
@Entity
@Getter
@NoArgsConstructor
public class Post {

    @Builder
    public Post(Season season, Department department, BoardFolder folder,
                String title, String content, Long createdByUserId) {
        this.season = season;
        this.department = department;
        this.folder = folder;
        this.title = title;
        this.content = content;
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

    /** Nullable — null means the post sits at the board root, unfiled. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id")
    private BoardFolder folder;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Long createdByUserId;

    /** Deleting a post cascades to its attachments and comments (and their attachments). */
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostAttachment> attachments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    private void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    private void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    /** Folder is metadata only — the underlying S3 keys never change on move. */
    public void moveToFolder(BoardFolder folder) {
        this.folder = folder;
    }
}
