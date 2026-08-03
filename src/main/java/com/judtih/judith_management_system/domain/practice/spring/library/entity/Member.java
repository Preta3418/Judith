package com.judtih.judith_management_system.domain.practice.spring.library.entity;

import com.judtih.judith_management_system.domain.practice.spring.library.enums.MemberTier;
import com.judtih.judith_management_system.domain.practice.spring.library.enums.Role;

import java.time.LocalDateTime;

/**
 * Library member. Loan limit depends on tier (BASIC=3, PREMIUM=10).
 * Fine balance accumulates from overdue returns and damaged copies.
 */
public class Member {

    private Long id;
    private String name;
    private String email;
    private MemberTier tier;
    private Role role;
    private boolean active;
    private long fineBalance;    // in KRW
    private LocalDateTime joinedAt;

    public Member() {}
    public Member(String name, String email, MemberTier tier, Role role) {
        this.name = name;
        this.email = email;
        this.tier = tier;
        this.role = role;
        this.active = true;
        this.fineBalance = 0;
        this.joinedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public MemberTier getTier() { return tier; }
    public Role getRole() { return role; }
    public boolean isActive() { return active; }
    public long getFineBalance() { return fineBalance; }
    public LocalDateTime getJoinedAt() { return joinedAt; }

    public void deactivate() { this.active = false; }
    public void reactivate() { this.active = true; }
    public void upgradeTier(MemberTier tier) { this.tier = tier; }
    public void addFine(long amount) { this.fineBalance += amount; }
    public void clearFine() { this.fineBalance = 0; }
}
