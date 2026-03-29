package com.gameboost.backend.controllers;

import com.gameboost.backend.dto.request.*;
import com.gameboost.backend.dto.response.*;
import com.gameboost.backend.models.*;
import com.gameboost.backend.services.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;
    private final MessageService messageService;

    @PostMapping("/request")
    public ResponseEntity<ApiResponse<Session>> createSession(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody SessionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Demande de session créée", sessionService.createSession(userDetails.getUsername(), request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSessions(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<SessionResponse> sessions = sessionService.getSessions(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Sessions récupérées", Map.of("sessions", sessions)));
    }

    @PatchMapping("/{id}/accept")
    public ResponseEntity<ApiResponse<Session>> acceptSession(
            @AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Session acceptée", sessionService.acceptSession(userDetails.getUsername(), id)));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<Session>> completeSession(
            @AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Session marquée comme terminée", sessionService.completeSession(userDetails.getUsername(), id)));
    }

    @PatchMapping("/{id}/confirm")
    public ResponseEntity<ApiResponse<Session>> confirmSession(
            @AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Session confirmée", sessionService.confirmSession(userDetails.getUsername(), id)));
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<ApiResponse<Session>> paySession(
            @AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Paiement simulé avec succès", sessionService.paySession(userDetails.getUsername(), id)));
    }

    @PostMapping("/{id}/review")
    public ResponseEntity<ApiResponse<Review>> reviewSession(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id, @Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Avis enregistré", sessionService.reviewSession(userDetails.getUsername(), id, request)));
    }

    @GetMapping("/{id}/messages")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMessages(
            @AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {
        List<MessageResponse> messages = messageService.getMessages(userDetails.getUsername(), id);
        return ResponseEntity.ok(ApiResponse.ok("Messages récupérés",
                Map.of("messages", messages, "total", messages.size())));
    }

    @PostMapping("/{id}/messages")
    public ResponseEntity<ApiResponse<Message>> sendMessage(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id, @Valid @RequestBody MessageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Message envoyé", messageService.sendMessage(userDetails.getUsername(), id, request)));
    }
}
