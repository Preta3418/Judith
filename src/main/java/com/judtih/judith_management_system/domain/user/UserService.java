package com.judtih.judith_management_system.domain.user;

import com.judtih.judith_management_system.domain.graduate.Graduate;
import com.judtih.judith_management_system.domain.graduate.GraduateRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final GraduateRepository graduateRepository;

    public UserService(UserRepository userRepository, GraduateRepository graduateRepository) {
        this.userRepository = userRepository;
        this.graduateRepository = graduateRepository;

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

    public Graduate graduateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id : " + id));

        Graduate graduate = new Graduate();

        graduate.setName(user.getName());
        graduate.setPhoneNumber(user.getPhoneNumber());
        graduate.setStudentNumber(user.getStudentNumber());

        userRepository.deleteById(id);

        return graduateRepository.save(graduate);
    }
}
