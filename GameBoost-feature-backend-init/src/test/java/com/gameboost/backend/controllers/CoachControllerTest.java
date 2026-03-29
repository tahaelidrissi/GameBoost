package com.gameboost.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gameboost.backend.dto.request.CoachProfileRequest;
import com.gameboost.backend.dto.response.CoachResponse;
import com.gameboost.backend.models.CoachProfile;
import com.gameboost.backend.models.User;
import com.gameboost.backend.security.JwtAuthFilter;
import com.gameboost.backend.security.JwtUtils;
import com.gameboost.backend.security.UserDetailsServiceImpl;
import com.gameboost.backend.services.CoachService;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CoachController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("CoachController — Tests unitaires")
class CoachControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private CoachService coachService;
    @MockitoBean private JwtUtils jwtUtils;
    @MockitoBean private JwtAuthFilter jwtAuthFilter;
    @MockitoBean private UserDetailsServiceImpl userDetailsService;

    @Nested
    @DisplayName("GET /coaches")
    class GetCoachesEndpoint {

        @Test
        @DisplayName("Liste des coachs — retourne 200 avec données")
        @WithMockUser(username = "user@mail.com", roles = "JOUEUR")
        void getCoaches_returns200() throws Exception {
            CoachResponse coach = CoachResponse.builder()
                    .id(1L).username("coach1").email("coach@mail.com")
                    .gameTitle("LoL").rank("Diamond")
                    .hourlyRate(BigDecimal.valueOf(25))
                    .averageRating(4.5).totalReviews(10L).build();

            when(coachService.getAllApprovedCoaches(any(), any()))
                    .thenReturn(List.of(coach));

            mockMvc.perform(get("/coaches"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.coaches[0].username").value("coach1"))
                    .andExpect(jsonPath("$.data.total").value(1));
        }

        @Test
        @DisplayName("Filtrer par jeu et rang")
        @WithMockUser(username = "user@mail.com", roles = "JOUEUR")
        void getCoaches_withFilters() throws Exception {
            when(coachService.getAllApprovedCoaches("Valorant", "Immortal"))
                    .thenReturn(List.of());

            mockMvc.perform(get("/coaches")
                            .param("game", "Valorant")
                            .param("rank", "Immortal"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.total").value(0));
        }
    }

    @Nested
    @DisplayName("GET /coaches/{id}")
    class GetCoachByIdEndpoint {

        @Test
        @DisplayName("Détails d'un coach — retourne 200")
        @WithMockUser(username = "user@mail.com", roles = "JOUEUR")
        void getCoachById_returns200() throws Exception {
            CoachResponse coach = CoachResponse.builder()
                    .id(1L).username("coach1").gameTitle("CS2")
                    .rank("Global Elite").hourlyRate(BigDecimal.valueOf(40))
                    .averageRating(4.8).totalReviews(20L).build();

            when(coachService.getCoachById(1L)).thenReturn(coach);

            mockMvc.perform(get("/coaches/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.gameTitle").value("CS2"));
        }
    }

    @Nested
    @DisplayName("POST /coaches/profile")
    class CreateProfileEndpoint {

        @Test
        @DisplayName("Créer un profil coach — retourne 201")
        @WithMockUser(username = "coach@mail.com", roles = "COACH")
        void createProfile_returns201() throws Exception {
            CoachProfileRequest request = new CoachProfileRequest();
            request.setGameTitle("Valorant");
            request.setRank("Radiant");
            request.setHourlyRate(BigDecimal.valueOf(50));
            request.setBio("Pro player");

            User coachUser = User.builder().id(UUID.randomUUID())
                    .email("coach@mail.com").username("coach1")
                    .role(User.Role.COACH).isApproved(false).build();

            CoachProfile profile = CoachProfile.builder()
                    .id(1L).user(coachUser).gameTitle("Valorant")
                    .rank("Radiant").hourlyRate(BigDecimal.valueOf(50))
                    .bio("Pro player").build();

            when(coachService.createProfile(eq("coach@mail.com"), any())).thenReturn(profile);

            mockMvc.perform(post("/coaches/profile")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true));
        }
    }

    @Nested
    @DisplayName("GET /coaches/me/earnings")
    class GetEarningsEndpoint {

        @Test
        @DisplayName("Revenus du coach — retourne 200")
        @WithMockUser(username = "coach@mail.com", roles = "COACH")
        void getEarnings_returns200() throws Exception {
            when(coachService.getEarnings("coach@mail.com"))
                    .thenReturn(BigDecimal.valueOf(500));

            mockMvc.perform(get("/coaches/me/earnings"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.total_earnings").value(500));
        }
    }
}
