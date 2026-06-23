package com.judtih.judith_management_system.domain.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
/** Request body for POST /api/admin/seasons/{seasonId}/notifications; sourceType and notificationType are injected by the controller. */
public class DashboardNotificationRequest {

    String title;
    String content;
}
