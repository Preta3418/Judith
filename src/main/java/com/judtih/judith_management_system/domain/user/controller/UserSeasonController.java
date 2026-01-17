package com.judtih.judith_management_system.domain.user.controller;

import com.judtih.judith_management_system.domain.user.dto.UpdateUserRolesRequest;
import com.judtih.judith_management_system.domain.user.dto.UserSeasonRequest;
import com.judtih.judith_management_system.domain.user.dto.UserSeasonResponse;
import com.judtih.judith_management_system.domain.user.service.UserSeasonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seasons")
@RequiredArgsConstructor
public class UserSeasonController {

    private final UserSeasonService service;

    @GetMapping("/{seasonId}/users")
    public ResponseEntity<List<UserSeasonResponse>> getUsersBySeason(@PathVariable Long seasonId) {

        return ResponseEntity.ok(service.getUsersBySeason(seasonId));

    }

    @PostMapping("/users")
    public ResponseEntity<UserSeasonResponse> addUserToSeason(@RequestBody UserSeasonRequest request) {

        return ResponseEntity.status(201).body(service.addUserToSeason(request));
    }

    @PutMapping("/users")
    public ResponseEntity<UserSeasonResponse> updateUserRole(@RequestBody UpdateUserRolesRequest request) {

        return ResponseEntity.status(200).body(service.updateUserRoles(request));
    }

    @DeleteMapping("/{seasonId}/users/{userId}")
    public ResponseEntity<Void> removeUserFromSeason(@PathVariable Long seasonId, @PathVariable Long userId) {

        service.removeUserFromSeason(userId, seasonId);
        return ResponseEntity.noContent().build();
    }



}
