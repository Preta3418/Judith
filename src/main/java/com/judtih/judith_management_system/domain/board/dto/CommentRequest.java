package com.judtih.judith_management_system.domain.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Create-comment payload — same multipart pattern as PostRequest:
 * this JSON is the "data" part, uploaded files come as the "files" part.
 * content is nullable (attachments-only comment allowed), but service rejects
 * a comment with neither content nor attachments.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {

    private String content;                          // nullable
    private List<AttachmentRequest> urlAttachments;  // nullable
}
