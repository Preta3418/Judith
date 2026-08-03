package com.judtih.judith_management_system.domain.practice.spring.library.enums;

/** Tier controls concurrent-loan limit. */
public enum MemberTier {
    BASIC(3),
    PREMIUM(10);

    private final int loanLimit;

    MemberTier(int loanLimit) { this.loanLimit = loanLimit; }
    public int getLoanLimit() { return loanLimit; }
}
