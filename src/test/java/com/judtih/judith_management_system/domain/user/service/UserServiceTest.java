package com.judtih.judith_management_system.domain.user.service;

import com.judtih.judith_management_system.domain.user.dto.UserRequest;
import com.judtih.judith_management_system.domain.user.dto.UserResponse;
import com.judtih.judith_management_system.domain.user.entity.User;
import com.judtih.judith_management_system.domain.user.enums.UserStatus;
import com.judtih.judith_management_system.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User activeUser() {
        return User.builder()
                .name("홍길동")
                .studentNumber("20240001")
                .phoneNumber("01012345678")
                .password("encodedPassword")
                .build();
        // status defaults to ACTIVE in the entity
    }

    private User inactiveUser() {
        User user = User.builder()
                .name("홍길동")
                .studentNumber("20240001")
                .phoneNumber("01012345678")
                .password("encodedPassword")
                .build();
        user.deactivate();
        return user;
    }

    @Test
    void createUser_shouldEncodeStudentNumberAsPassword() {
        UserRequest request = mock(UserRequest.class);
        when(request.getName()).thenReturn("홍길동");
        when(request.getStudentNumber()).thenReturn("20240001");
        when(request.getPhoneNumber()).thenReturn("01012345678");
        when(request.isAdmin()).thenReturn(false);
        when(passwordEncoder.encode("20240001")).thenReturn("encodedStudentNumber");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserResponse result = userService.createUser(request);

        verify(passwordEncoder).encode("20240001");
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("홍길동");
    }

    @Test
    void getUserById_shouldReturnResponse_whenFound() {
        User user = activeUser();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponse result = userService.getUserById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("홍길동");
    }

    @Test
    void getUserById_shouldThrow_whenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void deactivateUser_shouldSetInactive() {
        User user = activeUser();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponse result = userService.deactivateUser(1L);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(UserStatus.INACTIVE);
        assertThat(user.getStatus()).isEqualTo(UserStatus.INACTIVE);
    }

    @Test
    void reactivateUser_shouldSucceed_whenInactive() {
        User user = inactiveUser();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponse result = userService.reactivateUser(1L);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void reactivateUser_shouldThrow_whenNotInactive() {
        User user = activeUser();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.reactivateUser(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Only inactive users can be reactivated");
    }

    @Test
    void updateUser_shouldUpdateNameAndPhone() {
        User user = activeUser();

        UserRequest request = mock(UserRequest.class);
        when(request.getName()).thenReturn("김철수");
        when(request.getPhoneNumber()).thenReturn("01099999999");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponse result = userService.updateUser(1L, request);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("김철수");
        assertThat(result.getPhoneNumber()).isEqualTo("01099999999");
    }
}
