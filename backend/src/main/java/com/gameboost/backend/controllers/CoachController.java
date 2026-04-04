package com.gameboost.backend.controllers;

import com.gameboost.backend.dto.request.CoachProfileRequest;
import com.gameboost.backend.dto.response.*;
import com.gameboost.backend.models.CoachProfile;
import com.gameboost.backend.services.CoachService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/coaches")
@RequiredArgsConstructor
public class CoachController {

    private final CoachService coachService;

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCoaches(
            @RequestParam(required = false) String game,
            @RequestParam(required = false) String rank) {
        List<CoachResponse> coaches = coachService.getAllApprovedCoaches(game, rank);
        return ResponseEntity.ok(ApiResponse.ok("Liste des coachs",
                Map.of("coaches", coaches, "total", coaches.size())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CoachResponse>> getCoachById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Détails du coach", coachService.getCoachById(id)));
    }

    @PostMapping("/profile")
    @PreAuthorize("hasRole('COACH')")
    public ResponseEntity<ApiResponse<CoachProfile>> createProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CoachProfileRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Candidature soumise", coachService.createProfile(userDetails.getUsername(), request)));
    }

    @PutMapping("/me/profile")
    @PreAuthorize("hasRole('COACH')")
    public ResponseEntity<ApiResponse<CoachProfile>> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CoachProfileRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Profil mis à jour", coachService.updateProfile(userDetails.getUsername(), request)));
    }

    @GetMapping("/me/earnings")
    @PreAuthorize("hasRole('COACH')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getEarnings(
            @AuthenticationPrincipal UserDetails userDetails) {
        BigDecimal total = coachService.getEarnings(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Revenus récupérés", Map.of("total_earnings", total)));
    }
}
