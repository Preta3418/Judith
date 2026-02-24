package com.judtih.judith_management_system.global.security;

import com.judtih.judith_management_system.domain.season.Season;
import com.judtih.judith_management_system.domain.season.SeasonService;
import com.judtih.judith_management_system.domain.user.entity.User;
import com.judtih.judith_management_system.domain.user.repository.UserRepository;
import com.judtih.judith_management_system.domain.user.service.UserSeasonService;
import com.judtih.judith_management_system.global.security.dto.LoginRequest;
import com.judtih.judith_management_system.global.security.dto.LoginResponse;
import com.judtih.judith_management_system.global.security.event.UserLoggedInEvent;
import com.judtih.judith_management_system.global.security.exception.WrongUsernamePasswordException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final UserSeasonService userSeasonService;
    private final SeasonService seasonService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final ApplicationEventPublisher publisher;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {


        User user = userRepository.findByStudentNumber(request.getStudentNumber())
                .orElseThrow(() -> new WrongUsernamePasswordException("StudentNumber or Password was incorrect", 401, "Unauthorized"));


        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new WrongUsernamePasswordException("StudentNumber or Password was incorrect", 401, "Unauthorized");
        }


        boolean hasFullAccess = user.isAdmin();

        if (!hasFullAccess) {
            Season effectiveSeason = seasonService.findEffectiveSeasonForAccess().orElse(null);
            if (effectiveSeason != null) {
                hasFullAccess = userSeasonService.hasFullAccessRole(user.getId(), effectiveSeason.getId());
            }
        }


        String token = jwtUtil.generateToken(
                user.getId(),
                user.getStudentNumber(),
                hasFullAccess
        );

        LoginResponse response = LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .name(user.getName())
                .hasFullAccess(hasFullAccess)
                .passwordChanged(user.isPasswordChanged())
                .build();

        publisher.publishEvent(new UserLoggedInEvent(user));


        return ResponseEntity.ok(response);
    }


}
