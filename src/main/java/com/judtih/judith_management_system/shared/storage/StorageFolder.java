package com.judtih.judith_management_system.shared.storage;

public enum StorageFolder {
    EVENT_POSTER("Event_Poster"),
    PHOTOS("Photos"),
    VIDEOS("Video"),
    SCRIPT("Script"),
    ART("Art");



    private String folderName;

    StorageFolder(String folderName) {
        this.folderName = folderName;
    }


    public String getFolderName() {
        return folderName;
    }

}
