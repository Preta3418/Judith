package com.judtih.judith_management_system.domain.season;

/** Lifecycle status of a production season; only one season may be ACTIVE at a time. */
public enum Status {

    PREPARING, // season created but not yet started; members may be added without roles
    ACTIVE,    // season is running; all writes are permitted; only one allowed system-wide
    CLOSED     // season ended; fully read-only
}
