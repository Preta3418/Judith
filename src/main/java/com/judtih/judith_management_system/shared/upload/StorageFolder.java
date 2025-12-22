package com.judtih.judith_management_system.shared.upload;

import lombok.Getter;

@Getter
public enum StorageFolder {
    EVENT_POSTER("Event_Poster");

    private final String folderName;

    StorageFolder(String folderName) {
        this.folderName = folderName;
    }
}
