package com.judtih.judith_management_system.global.notification.enums;

/** Identifies which domain originated a notification, used with sourceId to link back to the source record. */
public enum SourceType {

    LMS,         // dashboard/season-related notification (sourceId = seasonId)
    SEASON,      // direct season management action
    RESERVATION, // ticketing event notification
    AUTH         // authentication-related (e.g., PASSWORD_NOT_CHANGED)
}
