package com.judtih.judith_management_system.domain.practice.spring.library.controller;

import com.judtih.judith_management_system.domain.practice.spring.library.dto.*;
import com.judtih.judith_management_system.domain.practice.spring.library.service.LoanService;

import java.util.List;

/**
 * PRACTICE — dispatch layer for the interesting business flows.
 * Add @RestController + @RequestMapping("/api/library") + @RequiredArgsConstructor when wiring.
 *
 * Endpoint conventions:
 *   POST   /loans                             borrow (BorrowRequest body)
 *   POST   /loans/{loanId}/return             return (ReturnRequest body)
 *   POST   /loans/{loanId}/extend             extend
 *   GET    /members/{memberId}/loans          my active loans
 *   POST   /reservations                      reserve (memberId, bookId params or body)
 *   DELETE /reservations/{id}                 cancel (needs caller id — normally from JWT)
 *   GET    /members/{memberId}/reservations
 *   GET    /admin/overdue-report              librarian only
 */
public class LoanController {

    private LoanService loanService;

    // ==================== EXAMPLE ====================

    /** POST /api/library/loans — borrow a book. */
    public LoanResponse borrow(BorrowRequest req) {
        return loanService.borrow(req.memberId, req.bookId);
    }

    // ==================== TODOs ====================

    /** TODO: POST /api/library/loans/{loanId}/return
     *  Body: ReturnRequest with the returned condition. */
    public LoanResponse returnBook(Long loanId, ReturnRequest req) {
        return null;
    }

    /** TODO: POST /api/library/loans/{loanId}/extend */
    public LoanResponse extendLoan(Long loanId) {
        return null;
    }

    /** TODO: POST /api/library/reservations?memberId=X&bookId=Y (or body) */
    public ReservationResponse reserve(Long memberId, Long bookId) {
        return null;
    }

    /** TODO: DELETE /api/library/reservations/{id}
     *  In real code, callerMemberId comes from the JWT; for the practice, take it as a query param. */
    public void cancelReservation(Long reservationId, Long callerMemberId) {
    }

    /** TODO: GET /api/library/members/{memberId}/loans  — the member's active loans. */
    public List<LoanResponse> myLoans(Long memberId) {
        return List.of();
    }

    /** TODO: GET /api/library/admin/overdue-report  — librarian only. */
    public List<OverdueReportRow> overdueReport() {
        return List.of();
    }
}
