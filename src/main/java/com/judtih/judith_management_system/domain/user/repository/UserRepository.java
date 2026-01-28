package com.judtih.judith_management_system.domain.user.repository;

import com.judtih.judith_management_system.domain.user.enums.UserStatus;
import com.judtih.judith_management_system.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByStatus(UserStatus status);

    List<User> findByStatusAndIsAdminFalse(UserStatus status);

    Optional<User> findByStudentNumber(String studentNumber);
}
