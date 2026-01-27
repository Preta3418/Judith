package com.judtih.judith_management_system.global.storage.service;
import com.judtih.judith_management_system.domain.season.Season;
import com.judtih.judith_management_system.domain.season.SeasonRepository;
import com.judtih.judith_management_system.domain.season.exception.NoSeasonFoundException;
import com.judtih.judith_management_system.global.storage.StorageFolder;
import com.judtih.judith_management_system.global.storage.dto.StoredFileResponse;
import com.judtih.judith_management_system.global.storage.entity.StoredFile;
import com.judtih.judith_management_system.global.storage.exception.FileStorageException;
import com.judtih.judith_management_system.global.storage.repository.StorageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class LocalStorageService implements StorageService {

    @Value("${upload.base-path}")
    private String basePath;

    private final StorageRepository repository;
    private final SeasonRepository seasonRepository;


    public StoredFileResponse uploadFile (MultipartFile file, StorageFolder folder, Long seasonId) {

        try {
            Path folderPath = Paths.get(basePath, folder.getFolderName());

            Files.createDirectories(folderPath);
            String filename = file.getOriginalFilename();

            if (filename == null) {
                throw new FileStorageException("could not find file name after upload", 500, "IO Error");
            }

            Path saveDir = folderPath.resolve(filename);
            file.transferTo(saveDir);

            String url = "/" + folder.getFolderName() + "/" + filename;

            Season season = seasonRepository.findById(seasonId)
                    .orElseThrow(() -> new NoSeasonFoundException("Season was not found with id: " + seasonId, 404, "Not Found"));
            StoredFile storedFile = new StoredFile(url, saveDir.getFileName().toString(), Files.size(saveDir), folder, season);
            repository.save(storedFile);

            return createStoredFileResponse(storedFile);

        } catch (IOException e) {
            throw new FileStorageException("failed to save file", 500, "IO Error", e);
        }
    }



    private StoredFileResponse createStoredFileResponse(StoredFile storedFile) {

        return StoredFileResponse.builder()
                .id(storedFile.getId())
                .url(storedFile.getUrl())
                .fileName(storedFile.getFileName())
                .fileSize(storedFile.getFileSize())
                .uploadedAt(storedFile.getUploadedAt())
                .build();
    }


}
