package com.judtih.judith_management_system.global.storage.entity;

import com.judtih.judith_management_system.domain.season.Season;
import com.judtih.judith_management_system.global.storage.StorageFolder;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class StoredFile {

    public StoredFile(String url, String fileName, Long fileSize, StorageFolder fileType, Season season) {
        this.url = url;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.season = season;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "season_id", nullable = false)
    private Season season;

    private LocalDateTime uploadedAt;

    @PrePersist
    private void onCreate() {
        uploadedAt = LocalDateTime.now();
    }

}
