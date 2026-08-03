package com.judtih.judith_management_system.domain.board.dto;

import com.judtih.judith_management_system.domain.board.enums.ContentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * One attachment as returned to the frontend. Used for both post attachments and
 * comment attachments — `source` + parent ids let the finder link back to where
 * the attachment lives ("게시물로 이동").
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentResponse {

    /** "POST" or "COMMENT" — which entity this attachment belongs to. */
    private String source;

    private Long id;
    private Long postId;
    private Long commentId;      // null for post attachments
    private ContentType contentType;
    private String fileUrl;
    private String fileName;
    private Long fileSize;
    private String linkUrl;
    private boolean isPinnedToDashboard;
    private LocalDateTime createdAt;
}
