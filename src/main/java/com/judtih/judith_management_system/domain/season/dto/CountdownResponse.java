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
/** Response for GET /api/public/seasons/countdown; days until the active season's performance date. */
public class CountdownResponse {

    int countdown;
    LocalDate eventDate;

}
