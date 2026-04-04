package com.gameboost.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gameboost.backend.dto.request.MessageRequest;
import com.gameboost.backend.dto.request.ReviewRequest;
import com.gameboost.backend.dto.request.SessionRequest;
import com.gameboost.backend.dto.response.MessageResponse;
import com.gameboost.backend.dto.response.SessionResponse;
import com.gameboost.backend.models.*;
import com.gameboost.backend.security.JwtAuthFilter;
import com.gameboost.backend.security.JwtUtils;
import com.gameboost.backend.security.UserDetailsServiceImpl;
import com.gameboost.backend.services.MessageService;
import com.gameboost.backend.services.SessionService;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SessionController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("SessionController — Tests unitaires")
class SessionControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private SessionService sessionService;
    @MockitoBean private MessageService messageService;
    @MockitoBean private JwtUtils jwtUtils;
    @MockitoBean private JwtAuthFilter jwtAuthFilter;
    @MockitoBean private UserDetailsServiceImpl userDetailsService;

    private Session createTestSession() {
        User player = User.builder().id(UUID.randomUUID())
                .email("player@mail.com").username("player1")
                .role(User.Role.JOUEUR).build();
        User coachUser = User.builder().id(UUID.randomUUID())
                .email("coach@mail.com").username("coach1")
                .role(User.Role.COACH).build();
        CoachProfile coach = CoachProfile.builder()
                .id(1L).user(coachUser).gameTitle("LoL")
                .rank("Diamond").hourlyRate(BigDecimal.valueOf(25)).build();

        return Session.builder()
                .id(1L).player(player).coach(coach)
                .status(Session.Status.REQUESTED)
                .durationHours(2).amount(BigDecimal.valueOf(50))
                .scheduledAt(LocalDateTime.now().plusDays(1)).build();
    }

    @Nested
    @DisplayName("POST /sessions/request")
    class CreateSessionEndpoint {

        @Test
        @DisplayName("Créer une session — retourne 201")
        @WithMockUser(username = "player@mail.com", roles = "JOUEUR")
        void createSession_returns201() throws Exception {
            SessionRequest request = new SessionRequest();
            request.setCoachId(1L);
            request.setDurationHours(2);
            request.setScheduledAt(LocalDateTime.now().plusDays(1));

            when(sessionService.createSession(eq("player@mail.com"), any()))
                    .thenReturn(createTestSession());

            mockMvc.perform(post("/sessions/request")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true));
        }
    }

    @Nested
    @DisplayName("GET /sessions")
    class GetSessionsEndpoint {

        @Test
        @DisplayName("Récupérer mes sessions — retourne 200")
        @WithMockUser(username = "player@mail.com", roles = "JOUEUR")
        void getSessions_returns200() throws Exception {
            SessionResponse resp = SessionResponse.builder()
                    .id(1L).status(Session.Status.REQUESTED)
                    .durationHours(2).amount(BigDecimal.valueOf(50)).build();

            when(sessionService.getSessions("player@mail.com"))
                    .thenReturn(List.of(resp));

            mockMvc.perform(get("/sessions"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.sessions").isArray());
        }
    }

    @Nested
    @DisplayName("PATCH /sessions/{id}/accept")
    class AcceptSessionEndpoint {

        @Test
        @DisplayName("Accepter une session — retourne 200")
        @WithMockUser(username = "coach@mail.com", roles = "COACH")
        void acceptSession_returns200() throws Exception {
            Session session = createTestSession();
            session.setStatus(Session.Status.ACCEPTED);
            when(sessionService.acceptSession("coach@mail.com", 1L)).thenReturn(session);

            mockMvc.perform(patch("/sessions/1/accept"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }
    }

    @Nested
    @DisplayName("PATCH /sessions/{id}/complete")
    class CompleteSessionEndpoint {

        @Test
        @DisplayName("Terminer une session — retourne 200")
        @WithMockUser(username = "coach@mail.com", roles = "COACH")
        void completeSession_returns200() throws Exception {
            Session session = createTestSession();
            session.setStatus(Session.Status.COMPLETED);
            when(sessionService.completeSession("coach@mail.com", 1L)).thenReturn(session);

            mockMvc.perform(patch("/sessions/1/complete"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("PATCH /sessions/{id}/confirm")
    class ConfirmSessionEndpoint {

        @Test
        @DisplayName("Confirmer une session — retourne 200")
        @WithMockUser(username = "player@mail.com", roles = "JOUEUR")
        void confirmSession_returns200() throws Exception {
            Session session = createTestSession();
            session.setStatus(Session.Status.CONFIRMED);
            when(sessionService.confirmSession("player@mail.com", 1L)).thenReturn(session);

            mockMvc.perform(patch("/sessions/1/confirm"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("POST /sessions/{id}/pay")
    class PaySessionEndpoint {

        @Test
        @DisplayName("Payer une session — retourne 200")
        @WithMockUser(username = "player@mail.com", roles = "JOUEUR")
        void paySession_returns200() throws Exception {
            Session session = createTestSession();
            session.setStatus(Session.Status.PAID);
            when(sessionService.paySession("player@mail.com", 1L)).thenReturn(session);

            mockMvc.perform(post("/sessions/1/pay"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("POST /sessions/{id}/review")
    class ReviewSessionEndpoint {

        @Test
        @DisplayName("Noter une session — retourne 201")
        @WithMockUser(username = "player@mail.com", roles = "JOUEUR")
        void reviewSession_returns201() throws Exception {
            ReviewRequest request = new ReviewRequest();
            request.setRating(5);
            request.setComment("Excellent!");

            Review review = Review.builder().id(1L).rating(5)
                    .comment("Excellent!").build();
            when(sessionService.reviewSession(eq("player@mail.com"), eq(1L), any()))
                    .thenReturn(review);

            mockMvc.perform(post("/sessions/1/review")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }
    }

    @Nested
    @DisplayName("Messages endpoints")
    class MessagesEndpoints {

        @Test
        @DisplayName("GET /sessions/{id}/messages — retourne 200")
        @WithMockUser(username = "player@mail.com", roles = "JOUEUR")
        void getMessages_returns200() throws Exception {
            MessageResponse msg = MessageResponse.builder()
                    .id(1L).senderUsername("coach1")
                    .content("Hello!").sentAt(LocalDateTime.now()).build();

            when(messageService.getMessages("player@mail.com", 1L))
                    .thenReturn(List.of(msg));

            mockMvc.perform(get("/sessions/1/messages"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.messages[0].content").value("Hello!"));
        }

        @Test
        @DisplayName("POST /sessions/{id}/messages — retourne 201")
        @WithMockUser(username = "player@mail.com", roles = "JOUEUR")
        void sendMessage_returns201() throws Exception {
            MessageRequest request = new MessageRequest();
            request.setContent("Bonjour!");

            User sender = User.builder().id(UUID.randomUUID())
                    .email("player@mail.com").username("player1").build();
            Session session = createTestSession();
            Message message = Message.builder().id(1L).session(session)
                    .sender(sender).content("Bonjour!").build();

            when(messageService.sendMessage(eq("player@mail.com"), eq(1L), any()))
                    .thenReturn(message);

            mockMvc.perform(post("/sessions/1/messages")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }
    }
}
