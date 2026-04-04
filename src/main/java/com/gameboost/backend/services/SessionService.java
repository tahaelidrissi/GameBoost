package com.gameboost.backend.services;

import com.gameboost.backend.dto.request.ReviewRequest;
import com.gameboost.backend.dto.request.SessionRequest;
import com.gameboost.backend.dto.response.SessionResponse;
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
public class SessionService {

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final CoachProfileRepository coachProfileRepository;
    private final ReviewRepository reviewRepository;

    public Session createSession(String playerEmail, SessionRequest request) {
        User player = getUser(playerEmail);
        if (player.getRole() != User.Role.JOUEUR)
            throw new IllegalArgumentException("Seuls les joueurs peuvent demander des sessions");

        CoachProfile coach = coachProfileRepository.findById(request.getCoachId())
                .orElseThrow(() -> new EntityNotFoundException("Coach non trouvé"));
        if (!coach.getUser().getIsApproved())
            throw new IllegalArgumentException("Ce coach n'est pas encore approuvé");

        BigDecimal amount = coach.getHourlyRate().multiply(BigDecimal.valueOf(request.getDurationHours()));

        Session session = Session.builder()
                .player(player).coach(coach).status(Session.Status.REQUESTED)
                .durationHours(request.getDurationHours()).amount(amount)
                .scheduledAt(request.getScheduledAt()).build();

        return sessionRepository.save(session);
    }

    public List<SessionResponse> getSessions(String email) {
        User user = getUser(email);
        List<Session> sessions;
        if (user.getRole() == User.Role.JOUEUR) {
            sessions = sessionRepository.findByPlayerOrderByCreatedAtDesc(user);
        } else {
            CoachProfile coach = coachProfileRepository.findByUser(user)
                    .orElseThrow(() -> new EntityNotFoundException("Profil coach non trouvé"));
            sessions = sessionRepository.findByCoachOrderByCreatedAtDesc(coach);
        }
        return sessions.stream().map(s -> toResponse(s, user.getRole())).collect(Collectors.toList());
    }

    public Session acceptSession(String coachEmail, Long sessionId) {
        Session session = getSession(sessionId);
        CoachProfile coach = getCoachProfile(coachEmail);
        if (!session.getCoach().getId().equals(coach.getId()))
            throw new IllegalArgumentException("Seul le coach concerné peut accepter cette session");
        assertStatus(session, Session.Status.REQUESTED, "acceptée");
        session.setStatus(Session.Status.ACCEPTED);
        return sessionRepository.save(session);
    }

    public Session completeSession(String coachEmail, Long sessionId) {
        Session session = getSession(sessionId);
        CoachProfile coach = getCoachProfile(coachEmail);
        if (!session.getCoach().getId().equals(coach.getId()))
            throw new IllegalArgumentException("Seul le coach concerné peut terminer cette session");
        assertStatus(session, Session.Status.ACCEPTED, "terminée");
        session.setStatus(Session.Status.COMPLETED);
        return sessionRepository.save(session);
    }

    public Session confirmSession(String playerEmail, Long sessionId) {
        Session session = getSession(sessionId);
        User player = getUser(playerEmail);
        if (!session.getPlayer().getId().equals(player.getId()))
            throw new IllegalArgumentException("Seul le joueur concerné peut confirmer cette session");
        assertStatus(session, Session.Status.COMPLETED, "confirmée");
        session.setStatus(Session.Status.CONFIRMED);
        return sessionRepository.save(session);
    }

    public Session paySession(String playerEmail, Long sessionId) {
        Session session = getSession(sessionId);
        User player = getUser(playerEmail);
        if (!session.getPlayer().getId().equals(player.getId()))
            throw new IllegalArgumentException("Seul le joueur concerné peut payer cette session");
        assertStatus(session, Session.Status.CONFIRMED, "payée");
        session.setStatus(Session.Status.PAID);
        return sessionRepository.save(session);
    }

    public Review reviewSession(String playerEmail, Long sessionId, ReviewRequest request) {
        Session session = getSession(sessionId);
        User player = getUser(playerEmail);
        if (!session.getPlayer().getId().equals(player.getId()))
            throw new IllegalArgumentException("Seul le joueur concerné peut noter cette session");
        if (session.getStatus() != Session.Status.PAID)
            throw new IllegalArgumentException("La session doit être payée avant de la noter");
        if (reviewRepository.existsBySession(session))
            throw new IllegalStateException("Vous avez déjà noté cette session");

        Review review = Review.builder().session(session)
                .rating(request.getRating()).comment(request.getComment()).build();
        return reviewRepository.save(review);
    }

    private void assertStatus(Session session, Session.Status expected, String action) {
        if (session.getStatus() != expected)
            throw new IllegalArgumentException("Cette session ne peut pas être " + action +
                    " (statut actuel : " + session.getStatus() + ")");
    }

    private SessionResponse toResponse(Session s, User.Role role) {
        SessionResponse.SessionResponseBuilder builder = SessionResponse.builder()
                .id(s.getId()).status(s.getStatus()).durationHours(s.getDurationHours())
                .amount(s.getAmount()).scheduledAt(s.getScheduledAt())
                .createdAt(s.getCreatedAt()).updatedAt(s.getUpdatedAt());

        if (role == User.Role.JOUEUR) {
            builder.coach(SessionResponse.CoachSummary.builder()
                    .id(s.getCoach().getId()).username(s.getCoach().getUser().getUsername())
                    .gameTitle(s.getCoach().getGameTitle()).build());
        } else {
            builder.player(SessionResponse.PlayerSummary.builder()
                    .id(s.getPlayer().getId().toString()).username(s.getPlayer().getUsername()).build());
        }
        return builder.build();
    }

    private Session getSession(Long id) {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Session non trouvée"));
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));
    }

    private CoachProfile getCoachProfile(String email) {
        return coachProfileRepository.findByUser(getUser(email))
                .orElseThrow(() -> new EntityNotFoundException("Profil coach non trouvé"));
    }
}
