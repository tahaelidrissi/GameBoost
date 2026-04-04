package com.gameboost.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gameboost.backend.dto.request.LoginRequest;
import com.gameboost.backend.dto.request.RegisterRequest;
import com.gameboost.backend.dto.request.UpdateUserRequest;
import com.gameboost.backend.dto.response.AuthResponse;
import com.gameboost.backend.models.User;
import com.gameboost.backend.security.JwtAuthFilter;
import com.gameboost.backend.security.JwtUtils;
import com.gameboost.backend.security.UserDetailsServiceImpl;
import com.gameboost.backend.services.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("AuthController — Tests unitaires")
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private AuthService authService;
    @MockitoBean private JwtUtils jwtUtils;
    @MockitoBean private JwtAuthFilter jwtAuthFilter;
    @MockitoBean private UserDetailsServiceImpl userDetailsService;

    @Nested
    @DisplayName("POST /auth/register")
    class RegisterEndpoint {

        @Test
        @DisplayName("Inscription réussie — retourne 201")
        void register_success_returns201() throws Exception {
            RegisterRequest request = new RegisterRequest();
            request.setEmail("new@mail.com");
            request.setPassword("Password1");
            request.setUsername("newuser");
            request.setRole(User.Role.JOUEUR);

            AuthResponse response = AuthResponse.builder()
                    .token("jwt-token").id(UUID.randomUUID())
                    .email("new@mail.com").username("newuser")
                    .role(User.Role.JOUEUR).isApproved(false).build();

            when(authService.register(any(RegisterRequest.class))).thenReturn(response);

            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.token").value("jwt-token"))
                    .andExpect(jsonPath("$.data.email").value("new@mail.com"));
        }
    }

    @Nested
    @DisplayName("POST /auth/login")
    class LoginEndpoint {

        @Test
        @DisplayName("Login réussi — retourne 200")
        void login_success_returns200() throws Exception {
            LoginRequest request = new LoginRequest();
            request.setEmail("test@mail.com");
            request.setPassword("Password1");

            AuthResponse response = AuthResponse.builder()
                    .token("jwt-token").id(UUID.randomUUID())
                    .email("test@mail.com").username("testuser")
                    .role(User.Role.JOUEUR).isApproved(false).build();

            when(authService.login(any(LoginRequest.class))).thenReturn(response);

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.token").value("jwt-token"));
        }
    }

    @Nested
    @DisplayName("GET /users/me")
    class GetProfileEndpoint {

        @Test
        @DisplayName("Récupérer le profil — retourne 200")
        @WithMockUser(username = "test@mail.com", roles = "JOUEUR")
        void getProfile_returns200() throws Exception {
            User user = User.builder()
                    .id(UUID.randomUUID()).email("test@mail.com")
                    .username("testuser").role(User.Role.JOUEUR)
                    .isApproved(false).build();

            when(authService.getProfile("test@mail.com")).thenReturn(user);

            mockMvc.perform(get("/users/me"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.email").value("test@mail.com"));
        }
    }

    @Nested
    @DisplayName("PUT /users/me")
    class UpdateProfileEndpoint {

        @Test
        @DisplayName("Mise à jour du profil — retourne 200")
        @WithMockUser(username = "test@mail.com", roles = "JOUEUR")
        void updateProfile_returns200() throws Exception {
            UpdateUserRequest request = new UpdateUserRequest();
            request.setUsername("newname");

            User updated = User.builder()
                    .id(UUID.randomUUID()).email("test@mail.com")
                    .username("newname").role(User.Role.JOUEUR)
                    .isApproved(false).build();

            when(authService.updateProfile(eq("test@mail.com"), any())).thenReturn(updated);

            mockMvc.perform(put("/users/me")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.username").value("newname"));
        }
    }
}
