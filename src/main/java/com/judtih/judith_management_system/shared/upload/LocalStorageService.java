package com.judtih.judith_management_system.shared.upload;
import com.judtih.judith_management_system.shared.upload.exception.FileStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class LocalStorageService implements StorageService{

    @Value("${upload.base-path}")
    private String basePath;


    public String uploadFile (MultipartFile file, StorageFolder folder) {

        try {
            Path folderPath = Paths.get(basePath, folder.getFolderName());

            Files.createDirectories(folderPath);
            String filename = file.getOriginalFilename();

            if (filename == null) {
                throw new FileStorageException("could not find file name after upload", 500, "IO Error");
            }

            Path saveDir = folderPath.resolve(filename);

            file.transferTo(saveDir);


            return "/" + folder.getFolderName() + "/" + filename;

        } catch (IOException e) {
            throw new FileStorageException("failed to save file", 500, "IO Error", e);
        }
    }


}
