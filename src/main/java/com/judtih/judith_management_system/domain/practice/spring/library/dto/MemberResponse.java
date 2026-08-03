package com.judtih.judith_management_system.domain.practice.spring.library.dto;

import com.judtih.judith_management_system.domain.practice.spring.library.enums.MemberTier;
import java.time.LocalDateTime;

public class MemberResponse {
    public Long id;
    public String name;
    public String email;
    public MemberTier tier;
    public boolean active;
    public long fineBalance;
    public int activeLoanCount;
    public int overdueLoanCount;
    public LocalDateTime joinedAt;
}
