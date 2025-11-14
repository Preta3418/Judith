package com.judtih.judith_management_system.domain.message.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//front end to back end
public class SendMessageRequest {
    @NotBlank
    @Size(max = 500)
    String messageContent;
}
