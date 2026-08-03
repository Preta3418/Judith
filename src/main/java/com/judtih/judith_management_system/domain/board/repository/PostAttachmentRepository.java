package com.judtih.judith_management_system.domain.board.repository;

import com.judtih.judith_management_system.domain.board.entity.PostAttachment;
import com.judtih.judith_management_system.domain.board.enums.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostAttachmentRepository extends JpaRepository<PostAttachment, Long> {

    List<PostAttachment> findByPostId(Long postId);

    /** Dashboard home "공유 파일" — pinned post attachments of one season+department.
     *  Property path walks PostAttachment.post.season.id and .post.department. */
    List<PostAttachment> findByPost_SeasonIdAndPost_DepartmentAndIsPinnedToDashboardTrue(Long seasonId, Department department);
}
