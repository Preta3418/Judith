package com.judtih.judith_management_system.domain.practice.spring.library.repository;

import com.judtih.judith_management_system.domain.practice.spring.library.entity.Reservation;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository {

    // ------- EXAMPLES -------

    /** All active (not cancelled, not fulfilled) reservations for a book, in queue order. */
    List<Reservation> findByBook_IdAndCancelledAtIsNullAndFulfilledFalseOrderByPositionAsc(Long bookId);


    // ------- TODOs -------

    /** TODO: The first (next-up) reservation for a book, or empty. Return Optional<Reservation>. */
    // ... write here

    /** TODO: All reservations for a member, most recent first. */
    // ... write here

    /** TODO: Has this member already reserved this book (and not cancelled/fulfilled)? Return boolean.
     *  Hint: existsBy... */
    // ... write here

    /** TODO: Count active reservations for a book — used to compute new reservation's position. */
    // ... write here
}
