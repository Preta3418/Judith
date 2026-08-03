package com.judtih.judith_management_system.domain.board.entity;

import com.judtih.judith_management_system.domain.board.enums.ContentType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * One file, external URL, or audio clip attached to a post.
 *
 * Field population by ContentType:
 *   FILE / AUDIO → fileUrl, fileName, fileSize set; linkUrl null
 *   URL          → linkUrl set; file fields null
 *
 * Pinning to the dashboard home is PER-ATTACHMENT (not per-post) — a post with
 * five files can have exactly one of them showcased on the home screen.
 * Only full-access members pin/unpin, via the bulk-pin endpoint.
 */
@Entity
@Getter
@NoArgsConstructor
public class PostAttachment {

    @Builder
    public PostAttachment(Post post, ContentType contentType,
                          String fileUrl, String fileName, Long fileSize, String linkUrl) {
        this.post = post;
        this.contentType = contentType;
        this.fileUrl = fileUrl;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.linkUrl = linkUrl;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentType contentType;

    private String fileUrl;
    private String fileName;
    private Long fileSize;
    private String linkUrl;

    @Column(nullable = false)
    private boolean isPinnedToDashboard = false;

    private LocalDateTime createdAt;

    @PrePersist
    private void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public void pin() {
        this.isPinnedToDashboard = true;
    }

    public void unpin() {
        this.isPinnedToDashboard = false;
    }
}
