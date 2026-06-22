package com.judtih.judith_management_system.domain.user.repository;

import com.judtih.judith_management_system.domain.user.enums.UserStatus;
import com.judtih.judith_management_system.domain.user.entity.User;
import com.judtih.judith_management_system.global.notification.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Repository for User entities; supports status-based filtering and student-number lookup for authentication. */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByStatus(UserStatus status);

    // Excludes admin accounts from member listings so they don't appear on the cast page
    List<User> findByStatusAndIsAdminFalse(UserStatus status);

    Optional<User> findByStudentNumber(String studentNumber);

}
