package com.judtih.judith_management_system.domain.board.repository;

import com.judtih.judith_management_system.domain.board.entity.BoardFolder;
import com.judtih.judith_management_system.domain.board.enums.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardFolderRepository extends JpaRepository<BoardFolder, Long> {

    /** Folder list of one board, oldest first (creation order = stable display order). */
    List<BoardFolder> findBySeasonIdAndDepartmentOrderByCreatedAtAsc(Long seasonId, Department department);
}
