package com.gameboost.backend.repositories;

import com.gameboost.backend.models.CoachProfile;
import com.gameboost.backend.models.Session;
import com.gameboost.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findByPlayerOrderByCreatedAtDesc(User player);
    List<Session> findByCoachOrderByCreatedAtDesc(CoachProfile coach);
    List<Session> findByPlayerAndStatus(User player, Session.Status status);
    List<Session> findByCoachAndStatus(CoachProfile coach, Session.Status status);
}
