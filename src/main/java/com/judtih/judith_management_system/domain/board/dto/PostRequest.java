package com.judtih.judith_management_system.domain.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Create-post payload. Arrives as the "data" @RequestPart of a multipart request;
 * uploaded FILE/AUDIO attachments arrive alongside as the "files" part.
 * Department comes from the URL path, not this body.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostRequest {

    private String title;
    private String content;               // nullable
    private Long folderId;                // nullable — null = board root
    private List<AttachmentRequest> urlAttachments;  // nullable — external links only
}
