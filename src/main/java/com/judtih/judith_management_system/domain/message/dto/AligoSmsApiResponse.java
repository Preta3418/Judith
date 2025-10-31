package com.judtih.judith_management_system.domain.message.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AligoSmsApiResponse {
    @JsonProperty("result_code")
    private int resultCode;

    private String message;

    @JsonProperty("msg_id")
    private int msgId;

    @JsonProperty("success_cnt")
    private int successCnt;

    @JsonProperty("error_cnt")
    private int errorCnt;

    @JsonProperty("msg_type")
    private String msgType;
}
