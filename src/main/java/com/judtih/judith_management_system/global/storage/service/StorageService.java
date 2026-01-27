package com.judtih.judith_management_system.global.storage.service;

import com.judtih.judith_management_system.global.storage.StorageFolder;
import com.judtih.judith_management_system.global.storage.dto.StoredFileResponse;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    StoredFileResponse uploadFile (MultipartFile file, StorageFolder folder, Long seasonId);

}
