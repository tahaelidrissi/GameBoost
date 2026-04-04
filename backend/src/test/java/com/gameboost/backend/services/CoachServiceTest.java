package com.gameboost.backend.services;

import com.gameboost.backend.dto.request.CoachProfileRequest;
import com.gameboost.backend.dto.response.CoachResponse;
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
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CoachService — Tests unitaires")
class CoachServiceTest {

    @Mock private CoachProfileRepository coachProfileRepository;
    @Mock private UserRepository userRepository;
    @Mock private ReviewRepository reviewRepository;
    @Mock private SessionRepository sessionRepository;

    @InjectMocks
    private CoachService coachService;

    private User coachUser;
    private CoachProfile coachProfile;

    @BeforeEach
    void setUp() {
        coachUser = User.builder()
                .id(UUID.randomUUID()).email("coach@mail.com")
                .username("coach1").role(User.Role.COACH)
                .password("encoded").isApproved(true).build();

        coachProfile = CoachProfile.builder()
                .id(1L).user(coachUser).gameTitle("Valorant")
                .rank("Immortal").hourlyRate(BigDecimal.valueOf(30.00))
                .bio("Expert Valorant").build();
    }

    @Nested
    @DisplayName("GetAllApprovedCoaches")
    class GetAllApprovedCoachesTests {

        @Test
        @DisplayName("Récupérer tous les coachs approuvés — sans filtre")
        void getAllApprovedCoaches_noFilter_shouldReturnAll() {
            when(coachProfileRepository.findApprovedByGameAndRank(null, null))
                    .thenReturn(List.of(coachProfile));
            when(reviewRepository.findAverageRatingByCoach(coachProfile)).thenReturn(4.5);
            when(reviewRepository.countByCoach(coachProfile)).thenReturn(10L);

            List<CoachResponse> result = coachService.getAllApprovedCoaches(null, null);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getUsername()).isEqualTo("coach1");
            assertThat(result.get(0).getAverageRating()).isEqualTo(4.5);
            assertThat(result.get(0).getTotalReviews()).isEqualTo(10L);
        }

        @Test
        @DisplayName("Filtrer par jeu — passe le paramètre au repository")
        void getAllApprovedCoaches_filterByGame_shouldPassParam() {
            when(coachProfileRepository.findApprovedByGameAndRank("Valorant", null))
                    .thenReturn(List.of(coachProfile));
            when(reviewRepository.findAverageRatingByCoach(any())).thenReturn(null);
            when(reviewRepository.countByCoach(any())).thenReturn(null);

            List<CoachResponse> result = coachService.getAllApprovedCoaches("Valorant", null);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getAverageRating()).isEqualTo(0.0);
            assertThat(result.get(0).getTotalReviews()).isEqualTo(0L);
            verify(coachProfileRepository).findApprovedByGameAndRank("Valorant", null);
        }

        @Test
        @DisplayName("Aucun coach approuvé — retourne liste vide")
        void getAllApprovedCoaches_none_shouldReturnEmpty() {
            when(coachProfileRepository.findApprovedByGameAndRank(any(), any()))
                    .thenReturn(Collections.emptyList());

            List<CoachResponse> result = coachService.getAllApprovedCoaches(null, null);
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("GetCoachById")
    class GetCoachByIdTests {

        @Test
        @DisplayName("Récupérer un coach par ID — inclut les reviews")
        void getCoachById_found_shouldReturnWithReviews() {
            when(coachProfileRepository.findById(1L)).thenReturn(Optional.of(coachProfile));
            when(reviewRepository.findAverageRatingByCoach(coachProfile)).thenReturn(4.0);
            when(reviewRepository.countByCoach(coachProfile)).thenReturn(5L);
            when(reviewRepository.findByCoach(coachProfile)).thenReturn(Collections.emptyList());

            CoachResponse result = coachService.getCoachById(1L);

            assertThat(result.getUsername()).isEqualTo("coach1");
            assertThat(result.getGameTitle()).isEqualTo("Valorant");
            assertThat(result.getReviews()).isNotNull();
        }

        @Test
        @DisplayName("Coach introuvable — lève EntityNotFoundException")
        void getCoachById_notFound_shouldThrow() {
            when(coachProfileRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> coachService.getCoachById(99L))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("CreateProfile")
    class CreateProfileTests {

        @Test
        @DisplayName("Créer un profil coach — succès")
        void createProfile_success() {
            CoachProfileRequest request = new CoachProfileRequest();
            request.setGameTitle("CS2");
            request.setRank("Global Elite");
            request.setHourlyRate(BigDecimal.valueOf(40));
            request.setBio("Top frageur");

            when(userRepository.findByEmail("coach@mail.com")).thenReturn(Optional.of(coachUser));
            when(coachProfileRepository.findByUser(coachUser)).thenReturn(Optional.empty());
            when(coachProfileRepository.save(any(CoachProfile.class))).thenAnswer(inv -> {
                CoachProfile p = inv.getArgument(0);
                p.setId(2L);
                return p;
            });

            CoachProfile result = coachService.createProfile("coach@mail.com", request);

            assertThat(result.getGameTitle()).isEqualTo("CS2");
            assertThat(result.getRank()).isEqualTo("Global Elite");
        }

        @Test
        @DisplayName("Créer un profil — non-coach lève exception")
        void createProfile_nonCoach_shouldThrow() {
            User playerUser = User.builder().id(UUID.randomUUID())
                    .email("player@mail.com").role(User.Role.JOUEUR).build();
            CoachProfileRequest request = new CoachProfileRequest();
            request.setGameTitle("CS2");
            request.setRank("Gold");
            request.setHourlyRate(BigDecimal.valueOf(10));

            when(userRepository.findByEmail("player@mail.com")).thenReturn(Optional.of(playerUser));

            assertThatThrownBy(() -> coachService.createProfile("player@mail.com", request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Seuls les coachs");
        }

        @Test
        @DisplayName("Créer un profil — profil déjà existant lève exception")
        void createProfile_alreadyExists_shouldThrow() {
            CoachProfileRequest request = new CoachProfileRequest();
            request.setGameTitle("CS2");
            request.setRank("Gold");
            request.setHourlyRate(BigDecimal.valueOf(10));

            when(userRepository.findByEmail("coach@mail.com")).thenReturn(Optional.of(coachUser));
            when(coachProfileRepository.findByUser(coachUser)).thenReturn(Optional.of(coachProfile));

            assertThatThrownBy(() -> coachService.createProfile("coach@mail.com", request))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("profil coach existe déjà");
        }
    }

    @Nested
    @DisplayName("UpdateProfile")
    class UpdateProfileTests {

        @Test
        @DisplayName("Mettre à jour le profil — succès")
        void updateProfile_success() {
            CoachProfileRequest request = new CoachProfileRequest();
            request.setBio("Updated bio");
            request.setHourlyRate(BigDecimal.valueOf(50));

            when(userRepository.findByEmail("coach@mail.com")).thenReturn(Optional.of(coachUser));
            when(coachProfileRepository.findByUser(coachUser)).thenReturn(Optional.of(coachProfile));
            when(coachProfileRepository.save(any(CoachProfile.class))).thenAnswer(inv -> inv.getArgument(0));

            CoachProfile result = coachService.updateProfile("coach@mail.com", request);

            assertThat(result.getBio()).isEqualTo("Updated bio");
            assertThat(result.getHourlyRate()).isEqualByComparingTo(BigDecimal.valueOf(50));
        }

        @Test
        @DisplayName("Mettre à jour — coach non approuvé lève exception")
        void updateProfile_notApproved_shouldThrow() {
            coachUser.setIsApproved(false);
            CoachProfileRequest request = new CoachProfileRequest();
            request.setBio("New bio");

            when(userRepository.findByEmail("coach@mail.com")).thenReturn(Optional.of(coachUser));

            assertThatThrownBy(() -> coachService.updateProfile("coach@mail.com", request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("pas encore approuvé");
        }
    }

    @Nested
    @DisplayName("GetEarnings")
    class GetEarningsTests {

        @Test
        @DisplayName("Calculer les revenus — somme des sessions payées")
        void getEarnings_withPaidSessions_shouldSumAmounts() {
            Session s1 = Session.builder().amount(BigDecimal.valueOf(50)).build();
            Session s2 = Session.builder().amount(BigDecimal.valueOf(75)).build();

            when(userRepository.findByEmail("coach@mail.com")).thenReturn(Optional.of(coachUser));
            when(coachProfileRepository.findByUser(coachUser)).thenReturn(Optional.of(coachProfile));
            when(sessionRepository.findByCoachAndStatus(coachProfile, Session.Status.PAID))
                    .thenReturn(List.of(s1, s2));

            BigDecimal earnings = coachService.getEarnings("coach@mail.com");
            assertThat(earnings).isEqualByComparingTo(BigDecimal.valueOf(125));
        }

        @Test
        @DisplayName("Calculer les revenus — aucune session payée retourne 0")
        void getEarnings_noPaidSessions_shouldReturnZero() {
            when(userRepository.findByEmail("coach@mail.com")).thenReturn(Optional.of(coachUser));
            when(coachProfileRepository.findByUser(coachUser)).thenReturn(Optional.of(coachProfile));
            when(sessionRepository.findByCoachAndStatus(coachProfile, Session.Status.PAID))
                    .thenReturn(Collections.emptyList());

            BigDecimal earnings = coachService.getEarnings("coach@mail.com");
            assertThat(earnings).isEqualByComparingTo(BigDecimal.ZERO);
        }
    }
}
