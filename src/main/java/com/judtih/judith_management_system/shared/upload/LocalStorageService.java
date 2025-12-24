package com.judtih.judith_management_system.shared.upload;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;


@Service
public class LocalStorageService implements StorageService{

    private final Path baseLocation;

    public LocalStorageService(@Value("${upload.base-path}") String basePath) {
        this.baseLocation = Paths.get(basePath);
    }


    @Override
    public String store(MultipartFile file, StorageFolder folder) {

        try {
            Path folderPath = baseLocation.resolve(folder.getFolderName());

            Files.createDirectories(folderPath);

            String originalFileName = file.getOriginalFilename();
            String extension = originalFileName.substring(originalFileName.lastIndexOf("."));

            String newFilename = UUID.randomUUID().toString() + extension;

            Path filePath = folderPath.resolve(newFilename);

            file.transferTo(filePath);

            return "/" + folder.getFolderName() + "/" + newFilename;


        } catch (IOException e) {
            throw new RuntimeException("file save failed", e);
        }

    }
}
