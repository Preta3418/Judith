package com.judtih.judith_management_system.domain.season.dto;

import com.judtih.judith_management_system.domain.season.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SeasonRequest {

    String name;
    Status status;
    LocalDate startDate;
    LocalDate endDate;
    LocalDate eventDate;

}
