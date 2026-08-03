package com.judtih.judith_management_system.domain.board.repository;

import com.judtih.judith_management_system.domain.board.entity.CommentAttachment;
import com.judtih.judith_management_system.domain.board.enums.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentAttachmentRepository extends JpaRepository<CommentAttachment, Long> {

    List<CommentAttachment> findByCommentId(Long commentId);

    /** Dashboard home "공유 파일" — pinned comment attachments of one season+department.
     *  Property path walks CommentAttachment.comment.post.season.id etc. */
    List<CommentAttachment> findByComment_Post_SeasonIdAndComment_Post_DepartmentAndIsPinnedToDashboardTrue(Long seasonId, Department department);
}
