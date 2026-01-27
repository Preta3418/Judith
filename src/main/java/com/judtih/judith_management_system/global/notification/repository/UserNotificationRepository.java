package com.judtih.judith_management_system.global.notification.repository;


import com.judtih.judith_management_system.global.notification.entity.UserNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {

    List<UserNotification> findByUserId(Long userId);

    List<UserNotification> findByUserIdAndIsReadFalse(Long userId);

    Integer countByUserIdAndIsReadFalse(Long userId);
}
