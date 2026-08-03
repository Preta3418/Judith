package com.judtih.judith_management_system.domain.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {

    private Long id;
    private String content;
    private List<AttachmentResponse> attachments;
    private Long createdByUserId;
    private String createdByUserName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
