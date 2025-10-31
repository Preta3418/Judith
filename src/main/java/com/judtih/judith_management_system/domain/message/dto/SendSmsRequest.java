package com.judtih.judith_management_system.domain.message.dto;


import com.judtih.judith_management_system.domain.user.UserStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendSmsRequest {
    private String msg;

    private UserStatus userStatus;
}
