package com.gameboost.backend.repositories;

import com.gameboost.backend.models.CoachProfile;
import com.gameboost.backend.models.Review;
import com.gameboost.backend.models.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findBySession(Session session);
    boolean existsBySession(Session session);

    @Query("SELECT r FROM Review r WHERE r.session.coach = :coach ORDER BY r.createdAt DESC")
    List<Review> findByCoach(CoachProfile coach);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.session.coach = :coach")
    Double findAverageRatingByCoach(CoachProfile coach);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.session.coach = :coach")
    Long countByCoach(CoachProfile coach);
}
