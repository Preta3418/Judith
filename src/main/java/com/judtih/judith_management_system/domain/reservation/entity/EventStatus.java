package com.judtih.judith_management_system.domain.reservation.entity;

/** Lifecycle state of a public performance event; reservations are only accepted when OPEN. */
public enum EventStatus {
    OPEN,      // accepting reservations
    CLOSED,    // not yet open or manually closed
    COMPLETED  // show has passed
}
