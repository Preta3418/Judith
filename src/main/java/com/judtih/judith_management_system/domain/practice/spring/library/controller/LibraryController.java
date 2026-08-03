package com.judtih.judith_management_system.domain.practice.spring.library.controller;

import com.judtih.judith_management_system.domain.practice.spring.library.dto.BookRequest;
import com.judtih.judith_management_system.domain.practice.spring.library.dto.BookResponse;
import com.judtih.judith_management_system.domain.practice.spring.library.service.LibraryService;

import java.util.List;

/**
 * PRACTICE — book/copy/category endpoints. Add these annotations when wiring:
 *
 *   @RestController
 *   @RequiredArgsConstructor
 *   @RequestMapping("/api/library")
 *
 * And on each method: @GetMapping / @PostMapping / etc. with the path suffix.
 * See method comments for the intended path + HTTP method.
 */
public class LibraryController {

    private LibraryService libraryService;

    // ==================== EXAMPLE ====================

    /** GET /api/library/books/{bookId}  — return single book detail. */
    public BookResponse getBook(Long bookId) {
        return libraryService.getBook(bookId);
    }

    // ==================== TODOs ====================

    /** TODO — POST /api/library/books  (librarian only)
     *  Body: BookRequest.  Returns 201 Created with BookResponse. */
    public BookResponse createBook(BookRequest req) {
        return null;
    }

    /** TODO — PUT /api/library/books/{bookId}  (librarian only)
     *  Returns 204 No Content. */
    public void updateBook(Long bookId, BookRequest req) {
    }

    /** TODO — DELETE /api/library/books/{bookId}  (librarian only)
     *  Returns 204 No Content. */
    public void deleteBook(Long bookId) {
    }

    /** TODO — GET /api/library/books?title=xxx  — search. */
    public List<BookResponse> search(String title) {
        return List.of();
    }

    /** TODO — GET /api/library/books/category/{categoryId} */
    public List<BookResponse> byCategory(Long categoryId) {
        return List.of();
    }
}
