package com.judtih.judith_management_system.shared.upload;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@AllArgsConstructor
public class UploadController {

    private final StorageService storageService;

    @PostMapping("/poster")
    public ResponseEntity<Map<String, String>> uploadPoster(
            @RequestParam("file") MultipartFile file
    ) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "no file found"));
        }

        String url = storageService.store(file, StorageFolder.EVENT_POSTER);

        return ResponseEntity.ok(Map.of("url", url));
    }
}
