package com.judtih.judith_management_system.shared.upload;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    String store(MultipartFile file, StorageFolder folder);
}
