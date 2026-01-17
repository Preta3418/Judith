package com.judtih.judith_management_system.domain.user.dto;

import com.judtih.judith_management_system.domain.user.entity.User;
import com.judtih.judith_management_system.domain.user.enums.UserStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserResponse {
    private Long id;
    private String name;
    private String studentNumber;
    private String phoneNumber;
    private boolean isAdmin;
    private UserStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime graduatedAt;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .studentNumber(user.getStudentNumber())
                .phoneNumber(user.getPhoneNumber())
                .isAdmin(user.isAdmin())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .graduatedAt(user.getGraduatedAt())
                .build();
    }
}
