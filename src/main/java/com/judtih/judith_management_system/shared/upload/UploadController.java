package com.judtih.judith_management_system.shared.upload;


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

    @PostMapping("/{folder}")
    public ResponseEntity<UploadResponse> uploadFile(@RequestParam MultipartFile file, @PathVariable StorageFolder folder) {

        String url = service.uploadFile(file, folder);

        UploadResponse response = UploadResponse.builder()
                .url(url)
                .size(file.getSize())
                .folder(folder.getFolderName())
                .uploadTime(LocalDateTime.now())
                .build();

        return ResponseEntity.status(201).body(response);
    }






}
