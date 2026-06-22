package com.judtih.judith_management_system.global.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/** Response returned on successful login; the client uses hasFullAccess and passwordChanged to control UI state. */
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
