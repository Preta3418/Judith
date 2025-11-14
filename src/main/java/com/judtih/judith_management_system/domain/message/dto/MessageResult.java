package com.judtih.judith_management_system.domain.message.dto;


import com.judtih.judith_management_system.domain.message.entity.MessageFailure;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
//back end to front end
public class MessageResult {
    private int totalAttempted;
    private int successCount;
    private int failureCount;
    private List<MessageFailure> failures;
}
