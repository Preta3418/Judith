package com.judtih.judith_management_system.domain.user.controller;

import com.judtih.judith_management_system.domain.user.dto.UserRequest;
import com.judtih.judith_management_system.domain.user.dto.UserResponse;
import com.judtih.judith_management_system.domain.user.dto.UserSeasonResponse;
import com.judtih.judith_management_system.domain.user.service.UserSeasonService;
import com.judtih.judith_management_system.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserSeasonService userSeasonService;


    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/active")
    public ResponseEntity<List<UserResponse>> getActiveUsers() {
        return ResponseEntity.ok(userService.getActiveUsers());
    }

    @GetMapping("/graduated")
    public ResponseEntity<List<UserResponse>> getGraduatedUsers() {
        return ResponseEntity.ok(userService.getGraduatedUsers());
    }

    @GetMapping("/{userId}/seasons")
    public ResponseEntity<List<UserSeasonResponse>> getSeasonsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(userSeasonService.getSeasonsByUser(userId));
    }


    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @PostMapping("/{id}/graduate")
    public ResponseEntity<UserResponse> graduateUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.graduateUser(id));
    }

    @PostMapping("/{id}/reactivate")
    public ResponseEntity<UserResponse> reactivateUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.reactivateUser(id));
    }




}
