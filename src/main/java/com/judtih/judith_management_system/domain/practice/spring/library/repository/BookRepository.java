package com.judtih.judith_management_system.domain.practice.spring.library.repository;

import com.judtih.judith_management_system.domain.practice.spring.library.entity.Book;

import java.util.List;
import java.util.Optional;

/**
 * PRACTICE — to make this a real Spring Data JPA repository:
 *   1. Change `interface BookRepository` to `interface BookRepository extends JpaRepository<Book, Long>`
 *   2. Add @Repository annotation
 *   3. All the derived-query methods you fill in below will just work.
 *
 * Below: a few EXAMPLE method signatures already written, then TODOs for you.
 * Naming rules: findBy{Property}[Operator][And|Or...][OrderBy...]
 */
public interface BookRepository {

    // ------- EXAMPLES (already written, just here as reference) -------

    /** Find by exact ISBN (unique). */
    Optional<Book> findByIsbn(String isbn);

    /** All books in a category, newest first by published year. */
    List<Book> findByCategory_IdOrderByPublishedYearDesc(Long categoryId);


    // ------- TODOs — write the method signature -------

    /** TODO: Case-insensitive substring search on title. Return List<Book>. */
    // ... write here

    /** TODO: All books by a given author (exact match). Return List<Book>. */
    // ... write here

    /** TODO: Count how many books exist in a given category. Return long. */
    // ... write here

    /** TODO: Check if a book with the given ISBN already exists. Return boolean. */
    // ... write here

    /** TODO: Return List<Book> for books published between two years (inclusive). */
    // ... write here
}
