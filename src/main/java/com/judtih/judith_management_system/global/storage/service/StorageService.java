package com.judtih.judith_management_system.global.storage.service;

import com.judtih.judith_management_system.global.storage.StorageFolder;
import com.judtih.judith_management_system.global.storage.dto.StoredFileResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * Implemented by LocalStorageService (@Profile local) and S3StorageService (@Profile prod).
 * Spring injects the right one automatically — UploadController has zero environment-specific code.
 */
public interface StorageService {

    StoredFileResponse uploadFile (MultipartFile file, StorageFolder folder, Long seasonId);

}
