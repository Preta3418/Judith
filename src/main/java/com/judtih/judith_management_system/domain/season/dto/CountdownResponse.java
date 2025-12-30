package com.judtih.judith_management_system.domain.season.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountdownResponse {

    int countdown;
    LocalDate eventDate;

}
