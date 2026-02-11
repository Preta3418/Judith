package com.judtih.judith_management_system.global.security.event;

import com.judtih.judith_management_system.domain.user.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserLoggedInEvent {
    private final User user;

}
