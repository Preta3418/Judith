package com.judtih.judith_management_system.domain.user;

import com.judtih.judith_management_system.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByStatus(UserStatus status);

    List<User> findByRoleInAndStatus(List<UserRole> roles, UserStatus status);

}
