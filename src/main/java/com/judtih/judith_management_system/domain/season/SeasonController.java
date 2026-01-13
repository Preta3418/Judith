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
@RequestMapping("/api/season")
public class SeasonController {

    private final SeasonService service;


    //admin ///////////////////////////////////////////////////////////////
    @PostMapping()
    public ResponseEntity<SeasonResponse> createSeason(@RequestParam String name) {

        return ResponseEntity.status(201).body(service.createSeason(name));

    }

    @PutMapping
    public ResponseEntity<SeasonResponse> updateSeason(@RequestBody SeasonRequest request) {

        return ResponseEntity.status(200).body(service.updateSeason(request));

    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<SeasonResponse> activateSeason(@PathVariable Long id) {

        return ResponseEntity.status(200).body(service.activateSeason(id));
    }

    @PostMapping("/{id}/close")
    public ResponseEntity<SeasonResponse> closeSeason(@PathVariable Long id) {

        return ResponseEntity.status(200).body(service.closeSeason(id));
    }

    @DeleteMapping("/{id}")
    public void deleteSeason(@PathVariable Long id) {
        service.deleteSeason(id);

        //should not do anything right now
    }

    @GetMapping
    public ResponseEntity<List<SeasonResponse>> getAllSeason() {

        return ResponseEntity.ok(service.getAllSeason());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SeasonResponse> getSeason(@PathVariable Long id) {

        return ResponseEntity.ok(service.getSeason(id));
    }


    //user endpoint

    @GetMapping("/current")
    public ResponseEntity<SeasonResponse> getCurrentSeason() {

        return ResponseEntity.ok(service.getCurrentSeason());
    }

    @GetMapping("/countdown")
    public ResponseEntity<CountdownResponse> getCountDown() {

        return ResponseEntity.ok(service.getCountDown());
    }

}
