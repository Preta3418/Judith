package com.judtih.judith_management_system.domain.board.dto;

import com.judtih.judith_management_system.domain.board.enums.Department;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Post as returned to the frontend.
 * List endpoints omit `comments` (null) to keep payloads small;
 * the single-post endpoint includes the full comment list.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {

    private Long id;
    private Department department;
    private Long folderId;               // null = root
    private String folderName;           // null = root
    private String title;
    private String content;
    private List<AttachmentResponse> attachments;
    private int commentCount;
    private Long createdByUserId;
    private String createdByUserName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CommentResponse> comments;  // only populated on single-post fetch
}
