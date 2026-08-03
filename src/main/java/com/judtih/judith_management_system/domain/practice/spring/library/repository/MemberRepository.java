package com.judtih.judith_management_system.domain.practice.spring.library.repository;

import com.judtih.judith_management_system.domain.practice.spring.library.entity.Member;
import com.judtih.judith_management_system.domain.practice.spring.library.enums.MemberTier;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {

    // ------- EXAMPLES -------

    Optional<Member> findByEmail(String email);

    List<Member> findByActiveTrue();


    // ------- TODOs -------

    /** TODO: All members of a given tier. */
    // ... write here

    /** TODO: All members with fineBalance greater than a threshold, highest first. */
    // ... write here

    /** TODO: Count active members. */
    // ... write here
}
