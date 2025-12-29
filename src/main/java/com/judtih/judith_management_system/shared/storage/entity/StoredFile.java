package com.judtih.judith_management_system.shared.storage.entity;

import com.judtih.judith_management_system.shared.storage.StorageFolder;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class StoredFile {

    public StoredFile(String url, String fileName, Long fileSize, StorageFolder fileType, Long eventShowcaseId) {
        this.url = url;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.eventShowcaseId = eventShowcaseId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private Long fileSize;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StorageFolder fileType;

    @Column(nullable = false)
    private Long eventShowcaseId;

    private LocalDateTime uploadedAt;

    @PrePersist
    private void onCreate() {
        uploadedAt = LocalDateTime.now();
    }

}
