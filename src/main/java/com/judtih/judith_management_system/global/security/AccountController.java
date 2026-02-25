package com.judtih.judith_management_system.global.security;

import com.judtih.judith_management_system.domain.user.entity.User;
import com.judtih.judith_management_system.domain.user.exception.NoUserFoundException;
import com.judtih.judith_management_system.domain.user.repository.UserRepository;
import com.judtih.judith_management_system.global.security.dto.PasswordChangeRequest;
import com.judtih.judith_management_system.global.security.exception.WrongUsernamePasswordException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/password")
@RequiredArgsConstructor
public class AccountController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PutMapping
    @Transactional
    public ResponseEntity<Void> changePassword(@RequestBody PasswordChangeRequest request) {
        String studentNumber = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByStudentNumber(studentNumber)
                .orElseThrow(() -> new NoUserFoundException("user was not found with student number: " + studentNumber, 404, "Not Found"));

        if (passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            String newPassword = passwordEncoder.encode(request.getNewPassword());
            user.updatePassword(newPassword);
        } else throw new WrongUsernamePasswordException("Password is incorrect", 401, "Unauthorized");

        return ResponseEntity.noContent().build();

    }
}
