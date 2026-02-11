package com.judtih.judith_management_system.global.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private Long userId;
    private String name;
    private boolean hasFullAccess;
    private boolean passwordChanged;
}
