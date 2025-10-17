package com.judtih.judith_management_system.domain.user;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> getAllUser() {
        return userRepository.findAll();

    }

    public User updateUser(Long id, User userUpdates) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id : " + id));

        if (userUpdates.getName() != null) {
            existingUser.setName(userUpdates.getName());
        }
        if (userUpdates.getPhoneNumber() != null) {
            String numbersOnly = userUpdates.getPhoneNumber().replaceAll("-","");
            existingUser.setPhoneNumber(numbersOnly);
        }
        if (userUpdates.getRole() != null) {
            existingUser.setRole(userUpdates.getRole());
        }

        return userRepository.save(existingUser);
    }

    public User graduateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        user.setStatus(UserStatus.GRADUATED);
        user.setGraduatedAt(java.time.LocalDateTime.now());

        return userRepository.save(user);
    }

    public List<User> getActiveUsers() {
        return userRepository.findByStatus(UserStatus.ACTIVE);
    }

    public List<User> getGraduatedUsers() {
        return userRepository.findByStatus(UserStatus.GRADUATED);
    }


}
