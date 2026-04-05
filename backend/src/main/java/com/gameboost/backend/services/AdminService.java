package com.gameboost.backend.services;

import com.gameboost.backend.models.*;
import com.gameboost.backend.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final CoachProfileRepository coachProfileRepository;
    private final SessionRepository sessionRepository;

    public List<CoachProfile> getPendingCoaches() {
        return coachProfileRepository.findAll().stream()
                .filter(c -> !c.getUser().getIsApproved()).collect(Collectors.toList());
    }

    public User approveCoach(Long coachProfileId) {
        CoachProfile profile = coachProfileRepository.findById(coachProfileId)
                .orElseThrow(() -> new EntityNotFoundException("Coach non trouvé"));
        User user = profile.getUser();
        user.setIsApproved(true);
        return userRepository.save(user);
    }

    public void rejectCoach(Long coachProfileId) {
        CoachProfile profile = coachProfileRepository.findById(coachProfileId)
                .orElseThrow(() -> new EntityNotFoundException("Coach non trouvé"));
        coachProfileRepository.delete(profile);
        userRepository.delete(profile.getUser());
    }

    public Map<String, Object> getDashboardStats() {
        List<User> allUsers = userRepository.findAll();
        List<CoachProfile> allCoaches = coachProfileRepository.findAll();
        List<Session> allSessions = sessionRepository.findAll();

        long totalPlayers = allUsers.stream().filter(u -> u.getRole() == User.Role.JOUEUR).count();
        long totalCoaches = allUsers.stream().filter(u -> u.getRole() == User.Role.COACH).count();
        long approvedCoaches = allCoaches.stream().filter(c -> c.getUser().getIsApproved()).count();
        long completedSessions = allSessions.stream().filter(s -> s.getStatus() == Session.Status.PAID).count();
        BigDecimal totalRevenue = allSessions.stream().filter(s -> s.getStatus() == Session.Status.PAID)
                .map(Session::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("total_users", allUsers.size());
        stats.put("total_players", totalPlayers);
        stats.put("total_coaches", totalCoaches);
        stats.put("approved_coaches", approvedCoaches);
        stats.put("pending_coaches", totalCoaches - approvedCoaches);
        stats.put("total_sessions", allSessions.size());
        stats.put("completed_sessions", completedSessions);
        stats.put("total_revenue", totalRevenue);
        return stats;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
