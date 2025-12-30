package com.judtih.judith_management_system.domain.season.dto;

import com.judtih.judith_management_system.domain.season.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SeasonResponse {

    Long id;
    String name;
    Status status;
    LocalDate startDate;
    LocalDate endDate;

}
