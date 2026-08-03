package com.judtih.judith_management_system.domain.practice.spring.library.dto;

import java.time.LocalDateTime;

/** One row in the librarian's overdue report. */
public class OverdueReportRow {
    public Long loanId;
    public Long memberId;
    public String memberName;
    public String memberEmail;
    public String bookTitle;
    public LocalDateTime dueAt;
    public long daysOverdue;
    public long accruedFine;
}
