package com.judtih.judith_management_system.domain.practice.spring.library.dto;

/** Create/update payload for a Book. */
public class BookRequest {
    public String title;
    public String author;
    public String isbn;
    public Integer publishedYear;
    public Long categoryId;
}
