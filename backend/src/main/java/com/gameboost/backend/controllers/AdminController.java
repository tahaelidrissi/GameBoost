package com.gameboost.backend.controllers;

import com.gameboost.backend.dto.response.ApiResponse;
import com.gameboost.backend.models.*;
import com.gameboost.backend.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/coaches/pending")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPendingCoaches() {
        List<CoachProfile> pending = adminService.getPendingCoaches();
        return ResponseEntity.ok(ApiResponse.ok("Coachs en attente",
                Map.of("pending_coaches", pending, "total", pending.size())));
    }

    @PatchMapping("/coaches/{id}/approve")
    public ResponseEntity<ApiResponse<User>> approveCoach(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Coach approuvé avec succès", adminService.approveCoach(id)));
    }

    @PatchMapping("/coaches/{id}/reject")
    public ResponseEntity<ApiResponse<Void>> rejectCoach(@PathVariable Long id) {
        adminService.rejectCoach(id);
        return ResponseEntity.ok(ApiResponse.ok("Coach rejeté", null));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.ok("Statistiques récupérées",
                Map.of("statistics", adminService.getDashboardStats())));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.ok("Utilisateurs récupérés", adminService.getAllUsers()));
    }
}
