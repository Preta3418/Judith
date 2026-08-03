package com.judtih.judith_management_system.domain.practice.spring.library.entity;

/**
 * Logical book (title/author/ISBN). Multiple BookCopy rows can point at one Book.
 * PRACTICE: real @Entity would add @ManyToOne on category and @OneToMany List<BookCopy> if you need it.
 */
public class Book {

    private Long id;
    private String title;
    private String author;
    private String isbn;
    private Integer publishedYear;
    private Category category;   // ManyToOne

    public Book() {}
    public Book(String title, String author, String isbn, Integer publishedYear, Category category) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publishedYear = publishedYear;
        this.category = category;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }
    public Integer getPublishedYear() { return publishedYear; }
    public Category getCategory() { return category; }

    public void update(String title, String author, Integer publishedYear, Category category) {
        this.title = title;
        this.author = author;
        this.publishedYear = publishedYear;
        this.category = category;
    }
}
