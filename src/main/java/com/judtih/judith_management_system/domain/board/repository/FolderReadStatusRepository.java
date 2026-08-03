package com.judtih.judith_management_system.domain.board.repository;

import com.judtih.judith_management_system.domain.board.entity.FolderReadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FolderReadStatusRepository extends JpaRepository<FolderReadStatus, Long> {

    Optional<FolderReadStatus> findByUserIdAndFolderId(Long userId, Long folderId);

    /** Batch fetch for the folder list — one query instead of N when computing unread dots. */
    List<FolderReadStatus> findByUserIdAndFolderIdIn(Long userId, List<Long> folderIds);

    /** Cleanup when a folder is deleted. */
    void deleteByFolderId(Long folderId);
}
