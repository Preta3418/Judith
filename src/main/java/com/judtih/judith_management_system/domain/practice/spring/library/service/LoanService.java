package com.judtih.judith_management_system.domain.practice.spring.library.service;

import com.judtih.judith_management_system.domain.practice.spring.library.dto.*;
import com.judtih.judith_management_system.domain.practice.spring.library.entity.*;
import com.judtih.judith_management_system.domain.practice.spring.library.enums.*;
import com.judtih.judith_management_system.domain.practice.spring.library.exception.*;
import com.judtih.judith_management_system.domain.practice.spring.library.repository.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * PRACTICE — THE HARD ONE.
 *
 * All the interesting business rules live here. Each TODO method has a detailed
 * comment describing exactly what it must do and in what order — treat them as
 * a spec, not a hint.
 *
 * Wiring: add @Service, make repositories `private final`, use @Transactional
 * on every write method (borrow, returnBook, extendLoan, reserveBook, cancelReservation).
 * The guards (assertActive, assertNoOverdue) can be private helpers.
 */
public class LoanService {

    private LoanRepository loanRepository;
    private ReservationRepository reservationRepository;
    private BookCopyRepository bookCopyRepository;
    private BookRepository bookRepository;
    private MemberRepository memberRepository;

    // ==================== EXAMPLES ====================

    /** Compute fine for one loan. FINE_PER_DAY per day overdue. Returns 0 if not overdue. */
    private long computeFine(Loan loan) {
        LocalDateTime endpoint = loan.getReturnedAt() != null ? loan.getReturnedAt() : LocalDateTime.now();
        if (!endpoint.isAfter(loan.getDueAt())) return 0;
        long daysOver = Duration.between(loan.getDueAt(), endpoint).toDays();
        return daysOver * Loan.FINE_PER_DAY;
    }

    /** Reusable guard: throws if the member is inactive. Use at the top of any write method
     *  that requires the member to be able to act. */
    private void assertActive(Member member) {
        if (!member.isActive()) throw new MemberInactiveException(member.getId());
    }


    // ==================== TODOs — the meat of the practice ====================

    /**
     * TODO — borrow(memberId, bookId): the core write path.
     *
     * ORDER OF OPERATIONS (matters for clean error messages):
     *   1. Load Member (throw EntityNotFoundException if missing)
     *   2. assertActive(member)
     *   3. Load Book (throw EntityNotFoundException if missing)
     *   4. Check overdue: if loanRepository.findActiveOverdueByMember(memberId, now) is non-empty
     *      → OverdueBlockException
     *   5. Check limit: countActive vs member.getTier().getLoanLimit()
     *      → if at or over → LoanLimitExceededException(limit)
     *   6. Pick an AVAILABLE copy: bookCopyRepository.findFirstByBook_IdAndStatus(bookId, AVAILABLE)
     *      → if empty → BookNotAvailableException(bookId)
     *   7. Mark the copy BORROWED (copy.markBorrowed())
     *   8. Create + save a new Loan
     *   9. Return LoanResponse via toResponse(loan)
     */
    public LoanResponse borrow(Long memberId, Long bookId) {
        // ...
        return null;
    }

    /**
     * TODO — returnBook(loanId, condition): the trickiest one because it has to notify a waitlist.
     *
     * ORDER:
     *   1. Load Loan (throw EntityNotFoundException if missing)
     *   2. Reject if loan.getReturnedAt() != null (already returned) → IllegalStateException("Already returned")
     *   3. Update copy condition (copy.updateCondition(condition))
     *   4. If condition == DAMAGED:
     *        - copy.markLost()
     *        - member.addFine(some replacement cost, e.g., 20000)
     *      Else:
     *        - Check if any reservation is waiting for this book:
     *            reservationRepository.findFirstByBookAndActive(bookId)
     *          If yes:
     *            - copy.markReserved()
     *            - reservation.fulfill()
     *            - notifyWaitlist(reservation)  // stub — just log for now
     *          Else:
     *            - copy.markAvailable()
     *   5. Compute overdue fine via computeFine(loan) → member.addFine(fine)
     *   6. loan.markReturned()
     *   7. Return the LoanResponse
     */
    public LoanResponse returnBook(Long loanId, BookCondition returnedCondition) {
        // ...
        return null;
    }

    /**
     * TODO — extendLoan(loanId): extend the due date by 7 days.
     *
     * RULES (in order — reject at the first failure):
     *   1. Load Loan (or 404)
     *   2. Reject if already returned → IllegalStateException
     *   3. Reject if loan.isExtended() → ExtensionNotAllowedException("already extended once")
     *   4. Reject if there's any active reservation for this book →
     *      ExtensionNotAllowedException("someone is waiting for this book")
     *   5. loan.extend()
     */
    public LoanResponse extendLoan(Long loanId) {
        // ...
        return null;
    }

    /**
     * TODO — reserveBook(memberId, bookId): add member to the waitlist.
     *
     * ORDER:
     *   1. Load Member, assertActive
     *   2. Load Book (or 404)
     *   3. Reject if the member is currently borrowing this book (any active loan
     *      on a copy of this book) → IllegalStateException("You're already borrowing this book")
     *   4. Reject if the member already has an active reservation on this book
     *      → IllegalStateException("Already on the waitlist")
     *   5. Compute position = countActiveReservationsForBook(bookId) + 1
     *   6. Create + save Reservation
     *   7. Return ReservationResponse
     */
    public ReservationResponse reserveBook(Long memberId, Long bookId) {
        // ...
        return null;
    }

    /**
     * TODO — cancelReservation(reservationId, callerMemberId):
     *   1. Load reservation (or 404)
     *   2. Reject if callerMemberId != reservation.getMember().getId()
     *      → SecurityException("Not your reservation")   // real code: custom Business exception
     *   3. reservation.cancel()
     *   4. Move up everyone with a higher position on this book by 1
     *      (fetch active reservations for book with position > cancelled's position; for each, movePositionUp())
     */
    public void cancelReservation(Long reservationId, Long callerMemberId) {
        // ...
    }

    /** TODO — getMyActiveLoans(memberId): return List<LoanResponse> of the member's ACTIVE loans. */
    public List<LoanResponse> getMyActiveLoans(Long memberId) {
        // ...
        return List.of();
    }

    /** TODO — getMyReservations(memberId): return List<ReservationResponse> most recent first. */
    public List<ReservationResponse> getMyReservations(Long memberId) {
        // ...
        return List.of();
    }

    /**
     * TODO — getOverdueReport(): librarian-only.
     *
     * Fetch the raw rows via LoanRepository.findOverdueRawRows(now), then compute
     * daysOverdue and accruedFine for each. Return List<OverdueReportRow>.
     *
     * (Alternative: fetch full Loan entities via findActiveOverdue() and map — that's fine too,
     *  the @Query practice is optional.)
     */
    public List<OverdueReportRow> getOverdueReport() {
        // ...
        return List.of();
    }

    /**
     * Stub — pretend to notify. Real system might publish a Spring event / send email / SMS.
     */
    private void notifyWaitlist(Reservation r) {
        // System.out.println("[NOTIFY] " + r.getMember().getEmail() + " your book is ready: " + r.getBook().getTitle());
    }


    // ==================== DTO mapping ====================

    private LoanResponse toResponse(Loan loan) {
        LoanResponse r = new LoanResponse();
        r.id = loan.getId();
        r.memberId = loan.getMember().getId();
        r.memberName = loan.getMember().getName();
        r.bookId = loan.getBookCopy().getBook().getId();
        r.bookTitle = loan.getBookCopy().getBook().getTitle();
        r.bookCopyId = loan.getBookCopy().getId();
        r.borrowedAt = loan.getBorrowedAt();
        r.dueAt = loan.getDueAt();
        r.returnedAt = loan.getReturnedAt();
        r.status = loan.getStatus();
        r.overdue = loan.isOverdue();
        r.fineAmount = computeFine(loan);
        r.extended = loan.isExtended();
        return r;
    }

    private ReservationResponse toResponse(Reservation res) {
        ReservationResponse r = new ReservationResponse();
        r.id = res.getId();
        r.memberId = res.getMember().getId();
        r.bookId = res.getBook().getId();
        r.bookTitle = res.getBook().getTitle();
        r.position = res.getPosition();
        r.fulfilled = res.isFulfilled();
        r.reservedAt = res.getReservedAt();
        return r;
    }
}
