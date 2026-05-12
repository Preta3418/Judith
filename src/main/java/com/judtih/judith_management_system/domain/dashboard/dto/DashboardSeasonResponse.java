package com.judtih.judith_management_system.domain.dashboard.dto;

import com.judtih.judith_management_system.domain.season.Status;
import com.judtih.judith_management_system.domain.user.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Getter
@NoArgsConstructor
public class DashboardSeasonResponse {

    Long seasonId;
    String seasonName;
    Status status;
    LocalDate startDate;
    LocalDate endDate;
    LocalDate eventDate;
    Set<UserRole> myRoles;
    boolean myFullAccess;

    @Builder
    public DashboardSeasonResponse(Long seasonId, String seasonName, Status status,
                                   LocalDate startDate, LocalDate endDate, LocalDate eventDate,
                                   Set<UserRole> myRoles) {
        this.seasonId = seasonId;
        this.seasonName = seasonName;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.eventDate = eventDate;
        this.myRoles = myRoles;
        this.myFullAccess = UserRole.hasFullAccess(myRoles);
    }
}
