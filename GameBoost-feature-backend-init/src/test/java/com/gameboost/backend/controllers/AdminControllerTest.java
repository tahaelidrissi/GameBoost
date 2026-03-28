package com.gameboost.backend.controllers;

import com.gameboost.backend.models.CoachProfile;
import com.gameboost.backend.models.User;
import com.gameboost.backend.security.JwtAuthFilter;
import com.gameboost.backend.security.JwtUtils;
import com.gameboost.backend.security.UserDetailsServiceImpl;
import com.gameboost.backend.services.AdminService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.*;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("AdminController — Tests unitaires")
class AdminControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private AdminService adminService;
    @MockitoBean private JwtUtils jwtUtils;
    @MockitoBean private JwtAuthFilter jwtAuthFilter;
    @MockitoBean private UserDetailsServiceImpl userDetailsService;

    @Nested
    @DisplayName("GET /admin/coaches/pending")
    class GetPendingCoachesEndpoint {

        @Test
        @DisplayName("Récupère les coachs en attente — retourne 200")
        @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
        void getPendingCoaches_returns200() throws Exception {
            User coachUser = User.builder().id(UUID.randomUUID())
                    .email("coach@mail.com").username("coach1")
                    .role(User.Role.COACH).isApproved(false).build();
            CoachProfile profile = CoachProfile.builder()
                    .id(1L).user(coachUser).gameTitle("LoL")
                    .rank("Gold").hourlyRate(BigDecimal.valueOf(20)).build();

            when(adminService.getPendingCoaches()).thenReturn(List.of(profile));

            mockMvc.perform(get("/admin/coaches/pending"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.total").value(1));
        }
    }

    @Nested
    @DisplayName("PATCH /admin/coaches/{id}/approve")
    class ApproveCoachEndpoint {

        @Test
        @DisplayName("Approuve un coach — retourne 200")
        @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
        void approveCoach_returns200() throws Exception {
            User user = User.builder().id(UUID.randomUUID())
                    .email("coach@mail.com").username("coach1")
                    .role(User.Role.COACH).isApproved(true).build();

            when(adminService.approveCoach(1L)).thenReturn(user);

            mockMvc.perform(patch("/admin/coaches/1/approve").with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }
    }

    @Nested
    @DisplayName("PATCH /admin/coaches/{id}/reject")
    class RejectCoachEndpoint {

        @Test
        @DisplayName("Rejette un coach — retourne 200")
        @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
        void rejectCoach_returns200() throws Exception {
            mockMvc.perform(patch("/admin/coaches/1/reject").with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }
    }

    @Nested
    @DisplayName("GET /admin/dashboard")
    class DashboardEndpoint {

        @Test
        @DisplayName("Récupère les stats du dashboard — retourne 200")
        @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
        void getDashboard_returns200() throws Exception {
            Map<String, Object> stats = new LinkedHashMap<>();
            stats.put("total_users", 10);
            stats.put("total_players", 5L);
            stats.put("total_coaches", 3L);
            stats.put("total_revenue", BigDecimal.valueOf(500));

            when(adminService.getDashboardStats()).thenReturn(stats);

            mockMvc.perform(get("/admin/dashboard"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.statistics.total_users").value(10));
        }
    }

    @Nested
    @DisplayName("GET /admin/users")
    class GetAllUsersEndpoint {

        @Test
        @DisplayName("Récupère tous les utilisateurs — retourne 200")
        @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
        void getAllUsers_returns200() throws Exception {
            User user = User.builder().id(UUID.randomUUID())
                    .email("user@mail.com").username("user1")
                    .role(User.Role.JOUEUR).isApproved(true).build();

            when(adminService.getAllUsers()).thenReturn(List.of(user));

            mockMvc.perform(get("/admin/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }
    }
}
