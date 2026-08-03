package com.judtih.judith_management_system.domain.practice.spring.library.exception;

/** 409 — cannot extend: already extended once OR someone is waiting for this book. */
public class ExtensionNotAllowedException extends RuntimeException {
    public ExtensionNotAllowedException(String reason) { super("Cannot extend loan: " + reason); }
}
