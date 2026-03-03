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
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@Service
@Profile("prod")
@RequiredArgsConstructor
public class S3StorageService implements StorageService {

    private final S3Client s3Client;
    private final StorageRepository repository;
    private final SeasonRepository seasonRepository;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.defaultRegion}")
    private String region;

    @Override
    public StoredFileResponse uploadFile(MultipartFile file, StorageFolder folder, Long seasonId) {

        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new FileStorageException("could not find file name after upload", 500, "IO Error");
        }

        // S3 key: Event_Poster/3/poster.jpg
        String key = folder.getFolderName() + "/" + seasonId + "/" + filename;

        try {
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        } catch (IOException e) {
            throw new FileStorageException("failed to upload file to S3", 500, "IO Error", e);
        }

        String url = "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + key;

        Season season = seasonRepository.findById(seasonId)
                .orElseThrow(() -> new NoSeasonFoundException("Season was not found with id: " + seasonId, 404, "Not Found"));

        StoredFile storedFile = new StoredFile(url, filename, file.getSize(), folder, season);
        repository.save(storedFile);

        return StoredFileResponse.builder()
                .id(storedFile.getId())
                .url(storedFile.getUrl())
                .fileName(storedFile.getFileName())
                .fileSize(storedFile.getFileSize())
                .uploadedAt(storedFile.getUploadedAt())
                .build();
    }
}
