package com.judtih.judith_management_system.domain.board.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * A comment on a post. Effectively a mini-post: it can carry text AND multiple
 * attachments — because a real workflow is "designer uploads a draft → director
 * comments with a marked-up version", and both belong in the file finder.
 *
 * content is NULLABLE — a comment may be attachments-only.
 */
@Entity
@Getter
@NoArgsConstructor
public class Comment {

    @Builder
    public Comment(Post post, String content, Long createdByUserId) {
        this.post = post;
        this.content = content;
        this.createdByUserId = createdByUserId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    /** Nullable — a comment can be just attachments. */
    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Long createdByUserId;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentAttachment> attachments = new ArrayList<>();

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

    public void update(String content) {
        this.content = content;
    }
}
