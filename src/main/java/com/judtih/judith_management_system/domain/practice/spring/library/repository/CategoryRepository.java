package com.judtih.judith_management_system.domain.practice.spring.library.repository;

import com.judtih.judith_management_system.domain.practice.spring.library.entity.Category;

import java.util.Optional;

public interface CategoryRepository {

    Optional<Category> findByName(String name);
    boolean existsByName(String name);
}
