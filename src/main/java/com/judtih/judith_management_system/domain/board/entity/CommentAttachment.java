package com.judtih.judith_management_system.domain.board.entity;

import com.judtih.judith_management_system.domain.board.enums.ContentType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Mirrors {@link PostAttachment} exactly, but belongs to a Comment.
 * Comment attachments appear in the same file finder and can be pinned to the
 * dashboard home just like post attachments.
 */
@Entity
@Getter
@NoArgsConstructor
public class CommentAttachment {

    @Builder
    public CommentAttachment(Comment comment, ContentType contentType,
                             String fileUrl, String fileName, Long fileSize, String linkUrl) {
        this.comment = comment;
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
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

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
