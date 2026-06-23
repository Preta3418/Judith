package com.judtih.judith_management_system.domain.calendar.enums;

/** Determines who can see a calendar event: the whole season cast or only the creator. */
public enum EventScope {
    SHARED,   // visible to all season members; only full-access roles can create
    PERSONAL  // visible only to the creating user; persists even in closed seasons
}
