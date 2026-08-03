package com.judtih.judith_management_system.domain.board.repository;

import com.judtih.judith_management_system.domain.board.entity.Post;
import com.judtih.judith_management_system.domain.board.enums.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    /** Board main feed — every post of one season+department, newest first. */
    List<Post> findBySeasonIdAndDepartmentOrderByCreatedAtDesc(Long seasonId, Department department);

    /** Folder view — posts filed in one folder, newest first. */
    List<Post> findByFolderIdOrderByCreatedAtDesc(Long folderId);

    /** Unread check — the single most recent post in a folder (compared against FolderReadStatus.lastViewedAt). */
    Optional<Post> findTopByFolderIdOrderByCreatedAtDesc(Long folderId);

    /** Root view — posts of one board that are NOT in any folder. */
    List<Post> findBySeasonIdAndDepartmentAndFolderIsNullOrderByCreatedAtDesc(Long seasonId, Department department);
}
