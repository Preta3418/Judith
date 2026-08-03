package com.judtih.judith_management_system.domain.practice.spring.library.entity;

/**
 * Book category (소설, 과학, 역사 ...).
 * PRACTICE: to make this a real JPA entity, add @Entity, @Id, @GeneratedValue, getters, no-args ctor.
 */
public class Category {

    private Long id;
    private String name;
    private String description;

    public Category() {}
    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }

    public void rename(String name) { this.name = name; }
}
