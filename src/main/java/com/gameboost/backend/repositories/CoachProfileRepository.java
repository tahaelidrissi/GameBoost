package com.gameboost.backend.repositories;

import com.gameboost.backend.models.CoachProfile;
import com.gameboost.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CoachProfileRepository extends JpaRepository<CoachProfile, Long> {
    Optional<CoachProfile> findByUser(User user);
    Optional<CoachProfile> findByUserId(UUID userId);

    @Query("SELECT c FROM CoachProfile c WHERE c.user.isApproved = true")
    List<CoachProfile> findAllApproved();

    @Query("SELECT c FROM CoachProfile c WHERE c.user.isApproved = true " +
           "AND (:game IS NULL OR c.gameTitle = :game) " +
           "AND (:rank IS NULL OR c.rank = :rank)")
    List<CoachProfile> findApprovedByGameAndRank(String game, String rank);
}
