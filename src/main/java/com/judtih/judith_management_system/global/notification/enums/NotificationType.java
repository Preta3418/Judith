package com.judtih.judith_management_system.global.notification.enums;

/** Categorises what a notification is about; used for filtering and conditional dispatch logic. */
public enum NotificationType {

    ANNOUNCEMENT,        // general admin message to members
    D_DAY_REMINDER,      // auto-sent near the performance date
    EVENT_REMINDER,      // reminder for a reservation event
    NEW_FILES_UPLOADED,  // triggered when scripts or assets are uploaded
    PASSWORD_NOT_CHANGED // sent at login if the user still has the default password
}
