package com.judtih.judith_management_system.global.security.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Request body for POST /api/public/auth/login; student number is used as the username. */
@Getter
@AllArgsConstructor
public class LoginRequest {
    private String studentNumber;
    private String password;
}
