package com.judtih.judith_management_system.domain.season.dto;

import com.judtih.judith_management_system.domain.season.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
/** Request body for creating or updating a Season; members list is used on creation to atomically add the initial cast. */
public class SeasonRequest {

    private Long id;
    private String name;
    private LocalDate eventDate;
    private List<SeasonMemberRequest> members;

}
