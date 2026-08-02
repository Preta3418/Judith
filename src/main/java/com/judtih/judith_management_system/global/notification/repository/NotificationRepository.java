package com.judtih.judith_management_system.global.notification.repository;

import com.judtih.judith_management_system.global.notification.entity.Notification;
import com.judtih.judith_management_system.global.notification.enums.SourceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Repository for bell-delivery Notification records; per-user read state is on UserNotificationRepository. */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    void deleteBySourceTypeAndSourceId(SourceType sourceType, Long sourceId);
}
