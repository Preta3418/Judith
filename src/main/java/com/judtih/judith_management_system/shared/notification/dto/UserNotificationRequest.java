package com.judtih.judith_management_system.shared.notification.dto;

import com.judtih.judith_management_system.domain.user.enums.UserRole;
import com.judtih.judith_management_system.shared.notification.enums.NotificationType;
import com.judtih.judith_management_system.shared.notification.enums.SourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserNotificationRequest {

    private String title;
    private String content;
    private NotificationType notificationType;
    private SourceType sourceType;
    private Long sourceId;
    private List<UserRole> targetRoles;

}
