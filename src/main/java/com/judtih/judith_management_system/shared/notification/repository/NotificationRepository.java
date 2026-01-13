package com.judtih.judith_management_system.shared.notification.repository;


import com.judtih.judith_management_system.shared.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {


}
