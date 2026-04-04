package com.gameboost.backend.services;

import com.gameboost.backend.dto.request.ReviewRequest;
import com.gameboost.backend.dto.request.SessionRequest;
import com.gameboost.backend.dto.response.SessionResponse;
import com.gameboost.backend.models.*;
import com.gameboost.backend.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SessionService — Tests unitaires")
class SessionServiceTest {

    @Mock private SessionRepository sessionRepository;
    @Mock private UserRepository userRepository;
    @Mock private CoachProfileRepository coachProfileRepository;
    @Mock private ReviewRepository reviewRepository;

    @InjectMocks
    private SessionService sessionService;

    private User player;
    private User coachUser;
    private CoachProfile coachProfile;
    private Session testSession;

    @BeforeEach
    void setUp() {
        player = User.builder()
                .id(UUID.randomUUID()).email("player@mail.com")
                .username("player1").role(User.Role.JOUEUR)
                .password("encoded").isApproved(true).build();

        coachUser = User.builder()
                .id(UUID.randomUUID()).email("coach@mail.com")
                .username("coach1").role(User.Role.COACH)
                .password("encoded").isApproved(true).build();

        coachProfile = CoachProfile.builder()
                .id(1L).user(coachUser).gameTitle("League of Legends")
                .rank("Diamond").hourlyRate(BigDecimal.valueOf(25.00))
                .bio("Pro coach").build();

        testSession = Session.builder()
                .id(1L).player(player).coach(coachProfile)
                .status(Session.Status.REQUESTED)
                .durationHours(2).amount(BigDecimal.valueOf(50.00))
                .scheduledAt(LocalDateTime.now().plusDays(1))
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("CreateSession")
    class CreateSessionTests {

        @Test
        @DisplayName("Créer une session — joueur valide et coach approuvé")
        void createSession_success() {
            SessionRequest request = new SessionRequest();
            request.setCoachId(1L);
            request.setDurationHours(2);
            request.setScheduledAt(LocalDateTime.now().plusDays(1));

            when(userRepository.findByEmail("player@mail.com")).thenReturn(Optional.of(player));
            when(coachProfileRepository.findById(1L)).thenReturn(Optional.of(coachProfile));
            when(sessionRepository.save(any(Session.class))).thenAnswer(inv -> {
                Session s = inv.getArgument(0);
                s.setId(1L);
                return s;
            });

            Session result = sessionService.createSession("player@mail.com", request);

            assertThat(result.getPlayer()).isEqualTo(player);
            assertThat(result.getCoach()).isEqualTo(coachProfile);
            assertThat(result.getStatus()).isEqualTo(Session.Status.REQUESTED);
            assertThat(result.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(50.00));
        }

        @Test
        @DisplayName("Créer une session — non-joueur lève IllegalArgumentException")
        void createSession_nonPlayer_shouldThrow() {
            SessionRequest request = new SessionRequest();
            request.setCoachId(1L);
            request.setDurationHours(2);
            request.setScheduledAt(LocalDateTime.now().plusDays(1));

            when(userRepository.findByEmail("coach@mail.com")).thenReturn(Optional.of(coachUser));

            assertThatThrownBy(() -> sessionService.createSession("coach@mail.com", request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Seuls les joueurs");
        }

        @Test
        @DisplayName("Créer une session — coach non approuvé lève exception")
        void createSession_coachNotApproved_shouldThrow() {
            coachUser.setIsApproved(false);
            SessionRequest request = new SessionRequest();
            request.setCoachId(1L);
            request.setDurationHours(2);
            request.setScheduledAt(LocalDateTime.now().plusDays(1));

            when(userRepository.findByEmail("player@mail.com")).thenReturn(Optional.of(player));
            when(coachProfileRepository.findById(1L)).thenReturn(Optional.of(coachProfile));

            assertThatThrownBy(() -> sessionService.createSession("player@mail.com", request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("pas encore approuvé");
        }

        @Test
        @DisplayName("Créer une session — coach inexistant lève EntityNotFoundException")
        void createSession_coachNotFound_shouldThrow() {
            SessionRequest request = new SessionRequest();
            request.setCoachId(99L);
            request.setDurationHours(2);
            request.setScheduledAt(LocalDateTime.now().plusDays(1));

            when(userRepository.findByEmail("player@mail.com")).thenReturn(Optional.of(player));
            when(coachProfileRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> sessionService.createSession("player@mail.com", request))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("AcceptSession")
    class AcceptSessionTests {

        @Test
        @DisplayName("Accepter une session — coach concerné avec statut REQUESTED")
        void acceptSession_success() {
            when(sessionRepository.findById(1L)).thenReturn(Optional.of(testSession));
            when(userRepository.findByEmail("coach@mail.com")).thenReturn(Optional.of(coachUser));
            when(coachProfileRepository.findByUser(coachUser)).thenReturn(Optional.of(coachProfile));
            when(sessionRepository.save(any(Session.class))).thenAnswer(inv -> inv.getArgument(0));

            Session result = sessionService.acceptSession("coach@mail.com", 1L);
            assertThat(result.getStatus()).isEqualTo(Session.Status.ACCEPTED);
        }

        @Test
        @DisplayName("Accepter une session — mauvais coach lève exception")
        void acceptSession_wrongCoach_shouldThrow() {
            CoachProfile otherCoach = CoachProfile.builder().id(99L).user(coachUser).build();

            when(sessionRepository.findById(1L)).thenReturn(Optional.of(testSession));
            when(userRepository.findByEmail("coach@mail.com")).thenReturn(Optional.of(coachUser));
            when(coachProfileRepository.findByUser(coachUser)).thenReturn(Optional.of(otherCoach));

            assertThatThrownBy(() -> sessionService.acceptSession("coach@mail.com", 1L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Seul le coach concerné");
        }

        @Test
        @DisplayName("Accepter une session — mauvais statut lève exception")
        void acceptSession_wrongStatus_shouldThrow() {
            testSession.setStatus(Session.Status.COMPLETED);

            when(sessionRepository.findById(1L)).thenReturn(Optional.of(testSession));
            when(userRepository.findByEmail("coach@mail.com")).thenReturn(Optional.of(coachUser));
            when(coachProfileRepository.findByUser(coachUser)).thenReturn(Optional.of(coachProfile));

            assertThatThrownBy(() -> sessionService.acceptSession("coach@mail.com", 1L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("ne peut pas être");
        }
    }

    @Nested
    @DisplayName("CompleteSession")
    class CompleteSessionTests {

        @Test
        @DisplayName("Terminer une session — coach concerné avec statut ACCEPTED")
        void completeSession_success() {
            testSession.setStatus(Session.Status.ACCEPTED);

            when(sessionRepository.findById(1L)).thenReturn(Optional.of(testSession));
            when(userRepository.findByEmail("coach@mail.com")).thenReturn(Optional.of(coachUser));
            when(coachProfileRepository.findByUser(coachUser)).thenReturn(Optional.of(coachProfile));
            when(sessionRepository.save(any(Session.class))).thenAnswer(inv -> inv.getArgument(0));

            Session result = sessionService.completeSession("coach@mail.com", 1L);
            assertThat(result.getStatus()).isEqualTo(Session.Status.COMPLETED);
        }

        @Test
        @DisplayName("Terminer une session — statut REQUESTED lève exception")
        void completeSession_wrongStatus_shouldThrow() {
            testSession.setStatus(Session.Status.REQUESTED);

            when(sessionRepository.findById(1L)).thenReturn(Optional.of(testSession));
            when(userRepository.findByEmail("coach@mail.com")).thenReturn(Optional.of(coachUser));
            when(coachProfileRepository.findByUser(coachUser)).thenReturn(Optional.of(coachProfile));

            assertThatThrownBy(() -> sessionService.completeSession("coach@mail.com", 1L))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("ConfirmSession")
    class ConfirmSessionTests {

        @Test
        @DisplayName("Confirmer une session — joueur concerné avec statut COMPLETED")
        void confirmSession_success() {
            testSession.setStatus(Session.Status.COMPLETED);

            when(sessionRepository.findById(1L)).thenReturn(Optional.of(testSession));
            when(userRepository.findByEmail("player@mail.com")).thenReturn(Optional.of(player));
            when(sessionRepository.save(any(Session.class))).thenAnswer(inv -> inv.getArgument(0));

            Session result = sessionService.confirmSession("player@mail.com", 1L);
            assertThat(result.getStatus()).isEqualTo(Session.Status.CONFIRMED);
        }

        @Test
        @DisplayName("Confirmer une session — mauvais joueur lève exception")
        void confirmSession_wrongPlayer_shouldThrow() {
            testSession.setStatus(Session.Status.COMPLETED);
            User otherPlayer = User.builder().id(UUID.randomUUID())
                    .email("other@mail.com").role(User.Role.JOUEUR).build();

            when(sessionRepository.findById(1L)).thenReturn(Optional.of(testSession));
            when(userRepository.findByEmail("other@mail.com")).thenReturn(Optional.of(otherPlayer));

            assertThatThrownBy(() -> sessionService.confirmSession("other@mail.com", 1L))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("PaySession")
    class PaySessionTests {

        @Test
        @DisplayName("Payer une session — joueur concerné avec statut CONFIRMED")
        void paySession_success() {
            testSession.setStatus(Session.Status.CONFIRMED);

            when(sessionRepository.findById(1L)).thenReturn(Optional.of(testSession));
            when(userRepository.findByEmail("player@mail.com")).thenReturn(Optional.of(player));
            when(sessionRepository.save(any(Session.class))).thenAnswer(inv -> inv.getArgument(0));

            Session result = sessionService.paySession("player@mail.com", 1L);
            assertThat(result.getStatus()).isEqualTo(Session.Status.PAID);
        }

        @Test
        @DisplayName("Payer une session — statut COMPLETED (pas CONFIRMED) lève exception")
        void paySession_wrongStatus_shouldThrow() {
            testSession.setStatus(Session.Status.COMPLETED);

            when(sessionRepository.findById(1L)).thenReturn(Optional.of(testSession));
            when(userRepository.findByEmail("player@mail.com")).thenReturn(Optional.of(player));

            assertThatThrownBy(() -> sessionService.paySession("player@mail.com", 1L))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("ReviewSession")
    class ReviewSessionTests {

        @Test
        @DisplayName("Noter une session — joueur concerné, session PAID, pas encore notée")
        void reviewSession_success() {
            testSession.setStatus(Session.Status.PAID);
            ReviewRequest request = new ReviewRequest();
            request.setRating(5);
            request.setComment("Excellent coach!");

            when(sessionRepository.findById(1L)).thenReturn(Optional.of(testSession));
            when(userRepository.findByEmail("player@mail.com")).thenReturn(Optional.of(player));
            when(reviewRepository.existsBySession(testSession)).thenReturn(false);
            when(reviewRepository.save(any(Review.class))).thenAnswer(inv -> {
                Review r = inv.getArgument(0);
                r.setId(1L);
                return r;
            });

            Review result = sessionService.reviewSession("player@mail.com", 1L, request);
            assertThat(result.getRating()).isEqualTo(5);
            assertThat(result.getComment()).isEqualTo("Excellent coach!");
        }

        @Test
        @DisplayName("Noter une session non payée — lève IllegalArgumentException")
        void reviewSession_notPaid_shouldThrow() {
            testSession.setStatus(Session.Status.COMPLETED);
            ReviewRequest request = new ReviewRequest();
            request.setRating(5);

            when(sessionRepository.findById(1L)).thenReturn(Optional.of(testSession));
            when(userRepository.findByEmail("player@mail.com")).thenReturn(Optional.of(player));

            assertThatThrownBy(() -> sessionService.reviewSession("player@mail.com", 1L, request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("payée");
        }

        @Test
        @DisplayName("Re-noter une session déjà notée — lève IllegalStateException")
        void reviewSession_alreadyReviewed_shouldThrow() {
            testSession.setStatus(Session.Status.PAID);
            ReviewRequest request = new ReviewRequest();
            request.setRating(3);

            when(sessionRepository.findById(1L)).thenReturn(Optional.of(testSession));
            when(userRepository.findByEmail("player@mail.com")).thenReturn(Optional.of(player));
            when(reviewRepository.existsBySession(testSession)).thenReturn(true);

            assertThatThrownBy(() -> sessionService.reviewSession("player@mail.com", 1L, request))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("déjà noté");
        }
    }

    @Nested
    @DisplayName("GetSessions")
    class GetSessionsTests {

        @Test
        @DisplayName("Récupérer les sessions d'un joueur")
        void getSessions_asPlayer_shouldReturnPlayerSessions() {
            when(userRepository.findByEmail("player@mail.com")).thenReturn(Optional.of(player));
            when(sessionRepository.findByPlayerOrderByCreatedAtDesc(player))
                    .thenReturn(List.of(testSession));

            List<SessionResponse> result = sessionService.getSessions("player@mail.com");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getCoach()).isNotNull();
            assertThat(result.get(0).getPlayer()).isNull();
        }

        @Test
        @DisplayName("Récupérer les sessions d'un coach")
        void getSessions_asCoach_shouldReturnCoachSessions() {
            when(userRepository.findByEmail("coach@mail.com")).thenReturn(Optional.of(coachUser));
            when(coachProfileRepository.findByUser(coachUser)).thenReturn(Optional.of(coachProfile));
            when(sessionRepository.findByCoachOrderByCreatedAtDesc(coachProfile))
                    .thenReturn(List.of(testSession));

            List<SessionResponse> result = sessionService.getSessions("coach@mail.com");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getPlayer()).isNotNull();
            assertThat(result.get(0).getCoach()).isNull();
        }

        @Test
        @DisplayName("Aucune session — retourne liste vide")
        void getSessions_noSessions_shouldReturnEmpty() {
            when(userRepository.findByEmail("player@mail.com")).thenReturn(Optional.of(player));
            when(sessionRepository.findByPlayerOrderByCreatedAtDesc(player))
                    .thenReturn(Collections.emptyList());

            List<SessionResponse> result = sessionService.getSessions("player@mail.com");
            assertThat(result).isEmpty();
        }
    }
}
