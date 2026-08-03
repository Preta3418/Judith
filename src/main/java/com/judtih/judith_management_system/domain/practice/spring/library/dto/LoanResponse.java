package com.judtih.judith_management_system.domain.practice.spring.library.dto;

import com.judtih.judith_management_system.domain.practice.spring.library.enums.LoanStatus;

import java.time.LocalDateTime;

public class LoanResponse {
    public Long id;
    public Long memberId;
    public String memberName;
    public Long bookId;
    public String bookTitle;
    public Long bookCopyId;
    public LocalDateTime borrowedAt;
    public LocalDateTime dueAt;
    public LocalDateTime returnedAt;
    public LoanStatus status;
    public boolean overdue;
    public long fineAmount;     // computed at read time for the caller's convenience
    public boolean extended;
}
