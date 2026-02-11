package com.judtih.judith_management_system.domain.season;

import com.judtih.judith_management_system.domain.season.dto.CountdownResponse;
import com.judtih.judith_management_system.domain.season.dto.SeasonRequest;
import com.judtih.judith_management_system.domain.season.dto.SeasonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SeasonController {

    private final SeasonService service;


    // ==================== Public Endpoints ====================

    @GetMapping("/api/public/seasons/current")
    public ResponseEntity<SeasonResponse> getCurrentSeason() {

        return ResponseEntity.ok(service.getCurrentSeason());
    }

    @GetMapping("/api/public/seasons/countdown")
    public ResponseEntity<CountdownResponse> getCountDown() {

        return ResponseEntity.ok(service.getCountDown());
    }


    // ==================== Admin Endpoints ====================

    @GetMapping("/api/admin/seasons")
    public ResponseEntity<List<SeasonResponse>> getAllSeason() {

        return ResponseEntity.ok(service.getAllSeason());
    }

    @GetMapping("/api/admin/seasons/{id}")
    public ResponseEntity<SeasonResponse> getSeason(@PathVariable Long id) {

        return ResponseEntity.ok(service.getSeason(id));
    }

    @PostMapping("/api/admin/seasons")
    public ResponseEntity<SeasonResponse> createSeason(@RequestBody SeasonRequest request) {

        return ResponseEntity.status(201).body(service.createSeason(request));

    }

    @PutMapping("/api/admin/seasons")
    public ResponseEntity<SeasonResponse> updateSeason(@RequestBody SeasonRequest request) {

        return ResponseEntity.status(200).body(service.updateSeason(request));

    }

    @PostMapping("/api/admin/seasons/{id}/activate")
    public ResponseEntity<SeasonResponse> activateSeason(@PathVariable Long id) {

        return ResponseEntity.status(200).body(service.activateSeason(id));
    }

    @PostMapping("/api/admin/seasons/{id}/close")
    public ResponseEntity<SeasonResponse> closeSeason(@PathVariable Long id) {

        return ResponseEntity.status(200).body(service.closeSeason(id));
    }

    @DeleteMapping("/api/admin/seasons/{id}")
    public void deleteSeason(@PathVariable Long id) {
        service.deleteSeason(id);

        //should not do anything right now
    }

}
