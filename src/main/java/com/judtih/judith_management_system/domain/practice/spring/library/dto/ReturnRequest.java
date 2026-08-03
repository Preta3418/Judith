package com.judtih.judith_management_system.domain.practice.spring.library.dto;

import com.judtih.judith_management_system.domain.practice.spring.library.enums.BookCondition;

/** POST body for /loans/{id}/return — reporter includes the returned condition. */
public class ReturnRequest {
    public BookCondition condition;
}
