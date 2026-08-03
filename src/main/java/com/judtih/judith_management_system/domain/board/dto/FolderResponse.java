package com.judtih.judith_management_system.domain.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FolderResponse {

    private Long id;
    private String name;
    private boolean hasUnread;   // true if any post in the folder is newer than the caller's lastViewedAt
    private int postCount;
    private Long createdByUserId;
    private LocalDateTime createdAt;
}
