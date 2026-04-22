package com.judtih.judith_management_system.global.notification.repository;


import com.judtih.judith_management_system.global.notification.entity.UserNotification;
import com.judtih.judith_management_system.global.notification.enums.NotificationType;
import com.judtih.judith_management_system.global.notification.enums.SourceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {

    List<UserNotification> findByUserId(Long userId);

    List<UserNotification> findByUserIdAndIsReadFalse(Long userId);

    Integer countByUserIdAndIsReadFalse(Long userId);

    boolean existsByUserIdAndIsReadFalseAndNotification_NotificationType(Long userId, NotificationType notificationType);

    @Query("SELECT un FROM UserNotification un " +
            "JOIN un.notification n " +
            "WHERE un.user.id = :userId " +
            "AND n.sourceType = :sourceType " +
            "AND n.sourceId = :sourceId")
    List<UserNotification> findSeasonNotifications(
            @Param("userId") Long userId,
            @Param("sourceType") SourceType sourceType,
            @Param("sourceId") Long sourceId);
}
