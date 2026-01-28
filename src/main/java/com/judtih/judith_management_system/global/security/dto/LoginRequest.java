package com.judtih.judith_management_system.global.security.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginRequest {
    private String studentNumber;
    private String password;
}
