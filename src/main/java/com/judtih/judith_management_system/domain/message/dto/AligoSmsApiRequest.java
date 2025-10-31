package com.judtih.judith_management_system.domain.message.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AligoSmsApiRequest {
    private String key;

    @JsonProperty("user_id")
    private String userId;

    private String sender;

    private String receiver;

    private String msg;
}
