package com.judtih.judith_management_system.domain.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * URL-type attachment sent inside PostRequest/CommentRequest JSON.
 * FILE/AUDIO attachments arrive as multipart files, NOT through this DTO —
 * this only carries external links.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentRequest {

    private String linkUrl;
}
