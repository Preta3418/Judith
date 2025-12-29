package com.judtih.judith_management_system.shared.storage.service;

import com.judtih.judith_management_system.shared.storage.StorageFolder;
import com.judtih.judith_management_system.shared.storage.dto.StoredFileResponse;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    StoredFileResponse uploadFile (MultipartFile file, StorageFolder folder);

}
