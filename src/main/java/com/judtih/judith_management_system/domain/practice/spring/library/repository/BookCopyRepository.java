package com.judtih.judith_management_system.domain.practice.spring.library.repository;

import com.judtih.judith_management_system.domain.practice.spring.library.entity.BookCopy;
import com.judtih.judith_management_system.domain.practice.spring.library.enums.BookStatus;

import java.util.List;
import java.util.Optional;

/**
 * PRACTICE — hint: property paths use underscore to navigate FKs.
 * `findByBook_Id(bookId)` means "BookCopy.book.id = ?".
 */
public interface BookCopyRepository {

    // ------- EXAMPLES -------

    /** All copies of a given book. */
    List<BookCopy> findByBook_Id(Long bookId);

    /** All AVAILABLE copies of a given book — used by borrow() to pick one. */
    List<BookCopy> findByBook_IdAndStatus(Long bookId, BookStatus status);


    // ------- TODOs -------

    /** TODO: Get the first AVAILABLE copy for a book, or empty. Return Optional<BookCopy>.
     *  Hint: findFirstBy... */
    // ... write here

    /** TODO: Count how many copies of a book have a given status. Return int. */
    // ... write here

    /** TODO: All copies with a given status across all books (for reports). */
    // ... write here
}
