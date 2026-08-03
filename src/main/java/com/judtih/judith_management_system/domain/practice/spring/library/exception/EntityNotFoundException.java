package com.judtih.judith_management_system.domain.practice.spring.library.exception;

/** 404 for missing entity. Real project would extend a BusinessException base. */
public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String what, Long id) { super(what + " not found: " + id); }
}
