package com.judtih.judith_management_system.domain.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Pin/unpin multiple attachments in one call — the ONLY pin mechanism (no
 * per-attachment endpoint). Full-access members only; enforced in the service.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BulkPinRequest {

    private List<Long> postAttachmentIds;     // nullable
    private List<Long> commentAttachmentIds;  // nullable
    private boolean pinned;                   // true = pin, false = unpin
}
