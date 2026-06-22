package com.judtih.judith_management_system.global.security.event;

import com.judtih.judith_management_system.domain.user.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** Spring application event published after a successful login; consumed by NotificationEventListener. */
@Getter
@RequiredArgsConstructor
public class UserLoggedInEvent {
    private final User user;

}
