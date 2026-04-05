package com.gameboost.backend.exceptions;

import com.gameboost.backend.controllers.AuthController;
import com.gameboost.backend.security.JwtAuthFilter;
import com.gameboost.backend.security.JwtUtils;
import com.gameboost.backend.security.UserDetailsServiceImpl;
import com.gameboost.backend.services.AuthService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("GlobalExceptionHandler — Tests unitaires")
class GlobalExceptionHandlerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private AuthService authService;
    @MockitoBean private JwtUtils jwtUtils;
    @MockitoBean private JwtAuthFilter jwtAuthFilter;
    @MockitoBean private UserDetailsServiceImpl userDetailsService;

    private static final String LOGIN_BODY =
            "{\"email\":\"user@mail.com\",\"password\":\"Password1\"}";

    @Nested
    @DisplayName("Gestion des exceptions métier")
    class BusinessExceptions {

        @Test
        @DisplayName("BadCredentialsException → 401 Unauthorized")
        void badCredentials_returns401() throws Exception {
            when(authService.login(any()))
                    .thenThrow(new BadCredentialsException("Email ou mot de passe incorrect"));

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(LOGIN_BODY))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Email ou mot de passe incorrect"));
        }

        @Test
        @DisplayName("IllegalArgumentException → 400 Bad Request")
        void illegalArgument_returns400() throws Exception {
            when(authService.login(any()))
                    .thenThrow(new IllegalArgumentException("Paramètre invalide"));

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(LOGIN_BODY))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Paramètre invalide"));
        }

        @Test
        @DisplayName("IllegalStateException → 409 Conflict")
        void illegalState_returns409() throws Exception {
            when(authService.login(any()))
                    .thenThrow(new IllegalStateException("Email déjà utilisé"));

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(LOGIN_BODY))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Email déjà utilisé"));
        }

        @Test
        @DisplayName("EntityNotFoundException → 404 Not Found")
        void entityNotFound_returns404() throws Exception {
            when(authService.login(any()))
                    .thenThrow(new EntityNotFoundException("Utilisateur non trouvé"));

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(LOGIN_BODY))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Utilisateur non trouvé"));
        }

        @Test
        @DisplayName("AccessDeniedException → 403 Forbidden")
        void accessDenied_returns403() throws Exception {
            when(authService.login(any()))
                    .thenThrow(new AccessDeniedException("Accès refusé"));

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(LOGIN_BODY))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Accès refusé"));
        }

        @Test
        @DisplayName("Exception générique → 500 Internal Server Error")
        void genericException_returns500() throws Exception {
            when(authService.login(any()))
                    .thenThrow(new RuntimeException("Erreur inattendue"));

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(LOGIN_BODY))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Erreur interne du serveur"));
        }
    }

    @Nested
    @DisplayName("Validation des champs (@Valid)")
    class ValidationExceptions {

        @Test
        @DisplayName("Corps JSON vide → 400 avec erreurs de validation")
        void emptyBody_returns400() throws Exception {
            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }
    }
}
