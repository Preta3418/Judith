package com.judtih.judith_management_system.shared.storage;


import com.judtih.judith_management_system.shared.storage.dto.StoredFileResponse;
import com.judtih.judith_management_system.shared.storage.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class UploadController {

    private final StorageService service;

    @PostMapping("/{folder}/season/{seasonId}")
    public ResponseEntity<StoredFileResponse> uploadFile(@RequestParam MultipartFile file, @PathVariable StorageFolder folder, @PathVariable Long seasonId) {

        StoredFileResponse response = service.uploadFile(file, folder, seasonId);


        return ResponseEntity.status(201).body(response);
    }






}
