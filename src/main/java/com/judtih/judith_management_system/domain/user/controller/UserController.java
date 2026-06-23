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

/** Admin endpoints for user management: create, update, activate/deactivate, and season membership queries. */
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserSeasonService userSeasonService;


    @GetMapping("/api/admin/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/api/admin/users/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/api/admin/users/active")
    public ResponseEntity<List<UserResponse>> getActiveUsers() {
        return ResponseEntity.ok(userService.getActiveUsers());
    }

    @GetMapping("/api/admin/users/inactive")
    public ResponseEntity<List<UserResponse>> getInactiveUsers() {
        return ResponseEntity.ok(userService.getInactiveUsers());
    }

    @GetMapping("/api/admin/users/{userId}/seasons")
    public ResponseEntity<List<UserSeasonResponse>> getSeasonsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(userSeasonService.getSeasonsByUser(userId));
    }


    @PostMapping("/api/admin/users")
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PutMapping("/api/admin/users/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @PostMapping("/api/admin/users/{id}/deactivate")
    public ResponseEntity<UserResponse> deactivateUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.deactivateUser(id));
    }

    @PostMapping("/api/admin/users/{id}/reactivate")
    public ResponseEntity<UserResponse> reactivateUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.reactivateUser(id));
    }




}
