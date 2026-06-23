package com.judtih.judith_management_system.domain.message.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/** Request body for POST /api/admin/messages/send-message; content capped at 500 characters per SNS SMS limits. */
@Getter
@Setter
public class SendMessageRequest {
    @NotBlank
    @Size(max = 500)
    private String messageContent;
}
