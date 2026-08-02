package com.judtih.judith_management_system.global.notification.repository;

import com.judtih.judith_management_system.global.notification.entity.UserNotification;
import com.judtih.judith_management_system.global.notification.enums.NotificationType;
import com.judtih.judith_management_system.global.notification.enums.SourceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {

    List<UserNotification> findByUserId(Long userId);

    List<UserNotification> findByUserIdAndIsReadFalse(Long userId);

    Integer countByUserIdAndIsReadFalse(Long userId);

    boolean existsByUserIdAndIsReadFalseAndNotification_NotificationType(Long userId, NotificationType notificationType);

    List<UserNotification> findByUserIdAndNotification_SourceTypeAndNotification_SourceIdIn(
            Long userId, SourceType sourceType, List<Long> sourceIds);

    void deleteByNotification_SourceTypeAndNotification_SourceId(SourceType sourceType, Long sourceId);
}
