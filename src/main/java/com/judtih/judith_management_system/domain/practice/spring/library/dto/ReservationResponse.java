package com.judtih.judith_management_system.domain.practice.spring.library.dto;

import java.time.LocalDateTime;

public class ReservationResponse {
    public Long id;
    public Long memberId;
    public Long bookId;
    public String bookTitle;
    public int position;
    public boolean fulfilled;
    public LocalDateTime reservedAt;
}
