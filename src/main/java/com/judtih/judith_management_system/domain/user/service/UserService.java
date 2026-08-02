package com.judtih.judith_management_system.domain.user.service;

import com.judtih.judith_management_system.domain.user.dto.UserRequest;
import com.judtih.judith_management_system.domain.user.dto.UserResponse;
import com.judtih.judith_management_system.domain.user.entity.User;
import com.judtih.judith_management_system.domain.user.enums.UserStatus;
import com.judtih.judith_management_system.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** CRUD service for user accounts; default password is the student number (BCrypt-encoded). */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    public UserResponse createUser(UserRequest request) {
        log.info("createUser: name={}, studentNumber={}", request.getName(), request.getStudentNumber());
        // Initial password is the student number; user is prompted to change it after first login
        String defaultPassword = passwordEncoder.encode(request.getStudentNumber());

        User user = User.builder()
                .name(request.getName())
                .studentNumber(request.getStudentNumber())
                .phoneNumber(request.getPhoneNumber().replaceAll("-", ""))
                .password(defaultPassword)
                .isAdmin(request.isAdmin())
                .build();

        User saved = userRepository.save(user);
        return createUserResponse(saved);
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        return createUserResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::createUserResponse)
                .toList();
    }

    public List<UserResponse> getActiveUsers() {
        return userRepository.findByStatusAndIsAdminFalse(UserStatus.ACTIVE).stream()
                .map(this::createUserResponse)
                .toList();
    }

    public List<UserResponse> getInactiveUsers() {
        return userRepository.findByStatusAndIsAdminFalse(UserStatus.INACTIVE).stream()
                .map(this::createUserResponse)
                .toList();
    }

    @Transactional
    public UserResponse updateUser(Long id, UserRequest request) {
        log.info("updateUser: id={}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        user.updateInfo(request.getName(), request.getPhoneNumber());
        return createUserResponse(user);
    }

    @Transactional
    public UserResponse deactivateUser(Long id) {
        log.info("deactivateUser: id={}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        user.deactivate();
        return createUserResponse(user);
    }

    @Transactional
    public UserResponse reactivateUser(Long id) {
        log.info("reactivateUser: id={}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        if (user.getStatus() != UserStatus.INACTIVE) {
            log.warn("reactivateUser: rejected — user {} is not INACTIVE (status={})", id, user.getStatus());
            throw new IllegalStateException("Only inactive users can be reactivated");
        }

        user.reactivate();
        return createUserResponse(user);
    }

    private UserResponse createUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .studentNumber(user.getStudentNumber())
                .phoneNumber(user.getPhoneNumber())
                .isAdmin(user.isAdmin())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .inactiveSince(user.getInactiveSince())
                .build();
    }
}
