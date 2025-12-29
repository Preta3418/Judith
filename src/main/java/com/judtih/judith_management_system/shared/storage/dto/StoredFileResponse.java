package com.judtih.judith_management_system.shared.storage.dto;

import com.judtih.judith_management_system.shared.storage.StorageFolder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StoredFileResponse {

    Long id;
    String url;
    String fileName;
    Long fileSize;
    StorageFolder folder;
    LocalDateTime uploadedAt;
}
