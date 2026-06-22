package com.judtih.judith_management_system.global.storage.dto;

import com.judtih.judith_management_system.global.storage.StorageFolder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
/** File metadata returned after upload and by dashboard script-listing endpoints. */
public class StoredFileResponse {

    Long id;
    String url;
    String fileName;
    Long fileSize;
    StorageFolder folder;
    LocalDateTime uploadedAt;
}
