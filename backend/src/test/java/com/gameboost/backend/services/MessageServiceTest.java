package com.gameboost.backend.services;

import com.gameboost.backend.dto.request.MessageRequest;
import com.gameboost.backend.dto.response.MessageResponse;
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
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MessageService — Tests unitaires")
class MessageServiceTest {

    @Mock private MessageRepository messageRepository;
    @Mock private SessionRepository sessionRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private MessageService messageService;

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
                .id(1L).user(coachUser).gameTitle("LoL")
                .rank("Diamond").hourlyRate(BigDecimal.valueOf(25)).build();

        testSession = Session.builder()
                .id(1L).player(player).coach(coachProfile)
                .status(Session.Status.ACCEPTED)
                .durationHours(2).amount(BigDecimal.valueOf(50))
                .scheduledAt(LocalDateTime.now().plusDays(1)).build();
    }

    @Nested
    @DisplayName("GetMessages")
    class GetMessagesTests {

        @Test
        @DisplayName("Récupérer les messages — joueur a accès")
        void getMessages_asPlayer_success() {
            Message msg = Message.builder().id(1L).session(testSession)
                    .sender(coachUser).content("Hello!").sentAt(LocalDateTime.now()).build();

            when(sessionRepository.findById(1L)).thenReturn(Optional.of(testSession));
            when(userRepository.findByEmail("player@mail.com")).thenReturn(Optional.of(player));
            when(messageRepository.findBySessionOrderBySentAtAsc(testSession)).thenReturn(List.of(msg));

            List<MessageResponse> result = messageService.getMessages("player@mail.com", 1L);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getContent()).isEqualTo("Hello!");
            assertThat(result.get(0).getSenderUsername()).isEqualTo("coach1");
        }

        @Test
        @DisplayName("Récupérer les messages — utilisateur non concerné lève AccessDeniedException")
        void getMessages_unauthorized_shouldThrow() {
            User stranger = User.builder().id(UUID.randomUUID())
                    .email("stranger@mail.com").username("stranger").role(User.Role.JOUEUR).build();

            when(sessionRepository.findById(1L)).thenReturn(Optional.of(testSession));
            when(userRepository.findByEmail("stranger@mail.com")).thenReturn(Optional.of(stranger));

            assertThatThrownBy(() -> messageService.getMessages("stranger@mail.com", 1L))
                    .isInstanceOf(AccessDeniedException.class);
        }

        @Test
        @DisplayName("Session introuvable — lève EntityNotFoundException")
        void getMessages_sessionNotFound_shouldThrow() {
            when(sessionRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> messageService.getMessages("player@mail.com", 99L))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("SendMessage")
    class SendMessageTests {

        @Test
        @DisplayName("Envoyer un message — session acceptée, joueur a accès")
        void sendMessage_success() {
            MessageRequest request = new MessageRequest();
            request.setContent("Bonjour coach!");

            when(sessionRepository.findById(1L)).thenReturn(Optional.of(testSession));
            when(userRepository.findByEmail("player@mail.com")).thenReturn(Optional.of(player));
            when(messageRepository.save(any(Message.class))).thenAnswer(inv -> {
                Message m = inv.getArgument(0);
                m.setId(1L);
                return m;
            });

            Message result = messageService.sendMessage("player@mail.com", 1L, request);

            assertThat(result.getContent()).isEqualTo("Bonjour coach!");
            assertThat(result.getSender()).isEqualTo(player);
        }

        @Test
        @DisplayName("Envoyer un message — session REQUESTED lève exception")
        void sendMessage_sessionNotAccepted_shouldThrow() {
            testSession.setStatus(Session.Status.REQUESTED);
            MessageRequest request = new MessageRequest();
            request.setContent("Hello");

            when(sessionRepository.findById(1L)).thenReturn(Optional.of(testSession));
            when(userRepository.findByEmail("player@mail.com")).thenReturn(Optional.of(player));

            assertThatThrownBy(() -> messageService.sendMessage("player@mail.com", 1L, request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("après acceptation");
        }

        @Test
        @DisplayName("Envoyer un message — coach a aussi accès")
        void sendMessage_asCoach_success() {
            MessageRequest request = new MessageRequest();
            request.setContent("Salut joueur!");

            when(sessionRepository.findById(1L)).thenReturn(Optional.of(testSession));
            when(userRepository.findByEmail("coach@mail.com")).thenReturn(Optional.of(coachUser));
            when(messageRepository.save(any(Message.class))).thenAnswer(inv -> {
                Message m = inv.getArgument(0);
                m.setId(2L);
                return m;
            });

            Message result = messageService.sendMessage("coach@mail.com", 1L, request);
            assertThat(result.getSender()).isEqualTo(coachUser);
        }
    }
}
