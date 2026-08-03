package com.judtih.judith_management_system.domain.practice.spring.library.service;

import com.judtih.judith_management_system.domain.practice.spring.library.dto.BookRequest;
import com.judtih.judith_management_system.domain.practice.spring.library.dto.BookResponse;
import com.judtih.judith_management_system.domain.practice.spring.library.entity.Book;
import com.judtih.judith_management_system.domain.practice.spring.library.entity.BookCopy;
import com.judtih.judith_management_system.domain.practice.spring.library.entity.Category;
import com.judtih.judith_management_system.domain.practice.spring.library.enums.BookCondition;
import com.judtih.judith_management_system.domain.practice.spring.library.enums.BookStatus;
import com.judtih.judith_management_system.domain.practice.spring.library.exception.EntityNotFoundException;
import com.judtih.judith_management_system.domain.practice.spring.library.repository.BookCopyRepository;
import com.judtih.judith_management_system.domain.practice.spring.library.repository.BookRepository;
import com.judtih.judith_management_system.domain.practice.spring.library.repository.CategoryRepository;

import java.util.List;

/**
 * PRACTICE — book/copy/category management. The easier of the two services.
 * The hard one is LoanService.
 *
 * To wire this as a real Spring service:
 *   1. Add @Service and @lombok.RequiredArgsConstructor annotations at the top
 *   2. Make the repository fields `private final` (Lombok will generate the ctor)
 *   3. Add @Transactional on write methods, and @Transactional(readOnly=true) at class level if you like
 */
public class LibraryService {

    private BookRepository bookRepository;
    private BookCopyRepository bookCopyRepository;
    private CategoryRepository categoryRepository;

    // ==================== EXAMPLE: fully implemented ====================

    /** Create a new book. Throws if the category doesn't exist. */
    public BookResponse createBook(BookRequest req) {
        Category category = categoryRepository.findByName(req.title == null ? "" : "")   // placeholder — real code would use req.categoryId
                .orElseThrow(() -> new EntityNotFoundException("Category", req.categoryId));
        Book book = new Book(req.title, req.author, req.isbn, req.publishedYear, category);
        // In real code: book = bookRepository.save(book);
        return toResponse(book);
    }

    /** DTO mapping — books show total and available copy counts. */
    private BookResponse toResponse(Book book) {
        BookResponse r = new BookResponse();
        r.id = book.getId();
        r.title = book.getTitle();
        r.author = book.getAuthor();
        r.isbn = book.getIsbn();
        r.publishedYear = book.getPublishedYear();
        r.categoryName = book.getCategory() != null ? book.getCategory().getName() : null;
        // Copy counts require queries — done here so callers get a complete DTO
        List<BookCopy> copies = bookCopyRepository.findByBook_Id(book.getId());
        r.totalCopies = copies.size();
        r.availableCopies = (int) copies.stream().filter(c -> c.getStatus() == BookStatus.AVAILABLE).count();
        return r;
    }


    // ==================== TODOs ====================

    /** TODO: update the book fields. Load book (throw EntityNotFoundException if missing),
     *  load the new category if categoryId changed, call book.update(...). */
    public void updateBook(Long bookId, BookRequest req) {
        // ...
    }

    /** TODO: delete a book. Business rule: reject if any of its copies are BORROWED.
     *  Otherwise deleteAll copies first, then delete the book. */
    public void deleteBook(Long bookId) {
        // ...
    }

    /** TODO: add a new copy of an existing book, in the given initial condition.
     *  Return the new copy's id. */
    public Long addCopy(Long bookId, BookCondition initialCondition) {
        // ...
        return null;
    }

    /** TODO: find books by title (case-insensitive contains). Return List<BookResponse>. */
    public List<BookResponse> searchByTitle(String keyword) {
        // ...
        return List.of();
    }

    /** TODO: get all books in a category. Return List<BookResponse>. */
    public List<BookResponse> getByCategory(Long categoryId) {
        // ...
        return List.of();
    }

    /** TODO: get a single book by id. Return BookResponse.
     *  If not found, throw EntityNotFoundException("Book", bookId). */
    public BookResponse getBook(Long bookId) {
        // ...
        return null;
    }

    /** TODO: create a category. Throw IllegalArgumentException if name is blank or already exists. */
    public Long createCategory(String name, String description) {
        // ...
        return null;
    }
}
