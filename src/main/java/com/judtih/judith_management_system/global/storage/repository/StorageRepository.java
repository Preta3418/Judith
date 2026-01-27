package com.judtih.judith_management_system.global.storage.repository;

import com.judtih.judith_management_system.global.storage.StorageFolder;
import com.judtih.judith_management_system.global.storage.entity.StoredFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StorageRepository extends JpaRepository<StoredFile, Long> {

    List<StoredFile> findByFileType(StorageFolder folder);

    List<StoredFile> findBySeasonId(Long seasonId);



}
