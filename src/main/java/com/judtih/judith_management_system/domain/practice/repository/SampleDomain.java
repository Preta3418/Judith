package com.judtih.judith_management_system.domain.practice.repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Toy domain for the repository/SQL practice files.
 * These are PLAIN CLASSES — no @Entity, no @Table — so Spring never touches them.
 * Pretend they're entities. In the quiz files, you'll write repository method names
 * as if they were.
 *
 * Pretend schema:
 *
 *   author (id, name, country, joined_at)
 *   book   (id, title, author_id [FK→author], price, published_at, deleted_at [nullable])
 *   library(id, name, city)
 *   library_book_stock (id, library_id [FK], book_id [FK], quantity)
 */
public class SampleDomain {

    public static class Author {
        public Long id;
        public String name;
        public String country;
        public LocalDateTime joinedAt;
    }

    public static class Book {
        public Long id;
        public String title;
        public Author author;          // ManyToOne
        public Integer price;
        public LocalDateTime publishedAt;
        public LocalDateTime deletedAt; // nullable; null = active
    }

    public static class Library {
        public Long id;
        public String name;
        public String city;
        public List<LibraryBookStock> stocks;
    }

    public static class LibraryBookStock {
        public Long id;
        public Library library;
        public Book book;
        public Integer quantity;
    }
}
