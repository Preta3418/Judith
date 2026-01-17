package com.judtih.judith_management_system.domain.user.service;

import com.judtih.judith_management_system.domain.user.dto.UserRequest;
import com.judtih.judith_management_system.domain.user.dto.UserResponse;
import com.judtih.judith_management_system.domain.user.entity.User;
import com.judtih.judith_management_system.domain.user.enums.UserStatus;
import com.judtih.judith_management_system.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserResponse createUser(UserRequest request) {
        User user = User.builder()
                .name(request.getName())
                .studentNumber(request.getStudentNumber())
                .phoneNumber(request.getPhoneNumber().replaceAll("-", ""))
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

    public List<UserResponse> getGraduatedUsers() {
        return userRepository.findByStatusAndIsAdminFalse(UserStatus.GRADUATED).stream()
                .map(this::createUserResponse)
                .toList();
    }

    @Transactional
    public UserResponse updateUser(Long id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        user.updateInfo(request.getName(), request.getPhoneNumber());
        return createUserResponse(user);
    }

    @Transactional
    public UserResponse graduateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        user.graduate();
        return createUserResponse(user);
    }

    @Transactional
    public UserResponse reactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        if (user.getStatus() != UserStatus.GRADUATED) {
            throw new IllegalStateException("Only graduated users can be reactivated");
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
                .graduatedAt(user.getGraduatedAt())
                .build();
    }
}
