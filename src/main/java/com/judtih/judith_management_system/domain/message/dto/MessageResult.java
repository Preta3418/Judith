package com.judtih.judith_management_system.domain.message.dto;


import com.judtih.judith_management_system.domain.message.entity.MessageFailure;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/** Response returned after a bulk SMS send, summarising success/failure counts and the failed records. */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResult {
    private int totalAttempted;
    private int successCount;
    private int failureCount;
    private List<MessageFailure> failures;
}
