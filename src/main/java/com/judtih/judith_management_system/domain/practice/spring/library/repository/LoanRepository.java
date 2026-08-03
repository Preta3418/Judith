package com.judtih.judith_management_system.domain.practice.spring.library.repository;

import com.judtih.judith_management_system.domain.practice.spring.library.entity.Loan;
import com.judtih.judith_management_system.domain.practice.spring.library.enums.LoanStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * The biggest repository — needs both derived queries and @Query for reports.
 */
public interface LoanRepository {

    // ------- EXAMPLES -------

    /** Member's ACTIVE loans (still checked out). Use this in borrow() to count against the limit. */
    List<Loan> findByMember_IdAndStatus(Long memberId, LoanStatus status);

    /** All loans of a book copy, newest first (usually just one active at a time). */
    List<Loan> findByBookCopy_IdOrderByBorrowedAtDesc(Long bookCopyId);


    // ------- TODOs — derived queries -------

    /** TODO: Count member's active loans. Return int. Faster than fetching all then .size(). */
    // ... write here

    /** TODO: All active loans for a member that are past their due date (before now).
     *  Hint: findByMember_IdAndStatusAndDueAtBefore(Long, LoanStatus, LocalDateTime) */
    // ... write here

    /** TODO: All loans (any member) that are ACTIVE and past due. Used by getOverdueReport. */
    // ... write here


    // ------- TODO — @Query needed -------

    /** TODO: Report — for each ACTIVE overdue loan, return (loanId, memberId, memberName, memberEmail,
     *  bookTitle, dueAt) as an Object[] projection. This is the raw data for OverdueReportRow;
     *  daysOverdue and accruedFine get computed in the service.
     *
     *  Signature to fill in:
     *    @Query("SELECT l.id, l.member.id, l.member.name, l.member.email, l.bookCopy.book.title, l.dueAt " +
     *           "FROM Loan l WHERE l.status = 'ACTIVE' AND l.dueAt < :now")
     *    List<Object[]> findOverdueRawRows(@Param("now") LocalDateTime now);
     */
}
