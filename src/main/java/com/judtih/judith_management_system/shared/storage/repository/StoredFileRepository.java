package com.judtih.judith_management_system.shared.storage.repository;

import com.judtih.judith_management_system.shared.storage.StorageFolder;
import com.judtih.judith_management_system.shared.storage.entity.StoredFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StorageRepository extends JpaRepository<StoredFile, Long> {

    List<StoredFile> findByFileType(StorageFolder folder);

    List<StoredFile> findByEventShowcaseId(Long id);



}
