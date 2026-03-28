package com.gameboost.backend.services;

import com.gameboost.backend.dto.request.CoachProfileRequest;
import com.gameboost.backend.dto.response.CoachResponse;
import com.gameboost.backend.dto.response.ReviewResponse;
import com.gameboost.backend.models.*;
import com.gameboost.backend.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CoachService {

    private final CoachProfileRepository coachProfileRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final SessionRepository sessionRepository;

    public List<CoachResponse> getAllApprovedCoaches(String game, String rank) {
        return coachProfileRepository.findApprovedByGameAndRank(game, rank)
                .stream().map(c -> toResponse(c, false)).collect(Collectors.toList());
    }

    public CoachResponse getCoachById(Long id) {
        CoachProfile coach = coachProfileRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Coach non trouvé"));
        return toResponse(coach, true);
    }

    public CoachProfile createProfile(String email, CoachProfileRequest request) {
        User user = getUser(email);
        if (user.getRole() != User.Role.COACH)
            throw new IllegalArgumentException("Seuls les coachs peuvent créer un profil coach");
        if (coachProfileRepository.findByUser(user).isPresent())
            throw new IllegalStateException("Un profil coach existe déjà pour cet utilisateur");

        CoachProfile profile = CoachProfile.builder()
                .user(user).gameTitle(request.getGameTitle()).rank(request.getRank())
                .bio(request.getBio()).hourlyRate(request.getHourlyRate())
                .proofImage(request.getProofImage()).build();

        return coachProfileRepository.save(profile);
    }

    public CoachProfile updateProfile(String email, CoachProfileRequest request) {
        User user = getUser(email);
        if (user.getRole() != User.Role.COACH)
            throw new IllegalArgumentException("Seuls les coachs peuvent modifier ce profil");
        if (!user.getIsApproved())
            throw new IllegalArgumentException("Votre profil n'est pas encore approuvé");

        CoachProfile profile = coachProfileRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("Profil coach non trouvé"));

        if (request.getBio() != null) profile.setBio(request.getBio());
        if (request.getRank() != null) profile.setRank(request.getRank());
        if (request.getHourlyRate() != null) profile.setHourlyRate(request.getHourlyRate());
        if (request.getGameTitle() != null) profile.setGameTitle(request.getGameTitle());

        return coachProfileRepository.save(profile);
    }

    public BigDecimal getEarnings(String email) {
        User user = getUser(email);
        CoachProfile coach = coachProfileRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("Profil coach non trouvé"));
        return sessionRepository.findByCoachAndStatus(coach, Session.Status.PAID)
                .stream().map(Session::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private CoachResponse toResponse(CoachProfile coach, boolean withReviews) {
        Double avg = reviewRepository.findAverageRatingByCoach(coach);
        Long count = reviewRepository.countByCoach(coach);

        CoachResponse.CoachResponseBuilder builder = CoachResponse.builder()
                .id(coach.getId()).username(coach.getUser().getUsername())
                .email(coach.getUser().getEmail()).gameTitle(coach.getGameTitle())
                .rank(coach.getRank()).bio(coach.getBio()).hourlyRate(coach.getHourlyRate())
                .averageRating(avg != null ? avg : 0.0).totalReviews(count != null ? count : 0L);

        if (withReviews) {
            List<ReviewResponse> reviews = reviewRepository.findByCoach(coach).stream()
                    .map(r -> ReviewResponse.builder().id(r.getId()).rating(r.getRating())
                            .comment(r.getComment()).createdAt(r.getCreatedAt()).build())
                    .collect(Collectors.toList());
            builder.reviews(reviews);
        }
        return builder.build();
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));
    }
}
