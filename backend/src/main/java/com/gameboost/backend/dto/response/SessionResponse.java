package com.gameboost.backend.dto.response;

import com.gameboost.backend.models.Session;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Builder
public class SessionResponse {
    private Long id;
    private Session.Status status;
    private Integer durationHours;
    private BigDecimal amount;
    private LocalDateTime scheduledAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private CoachSummary coach;
    private PlayerSummary player;

    @Data @Builder
    public static class CoachSummary {
        private Long id;
        private String username;
        private String gameTitle;
    }

    @Data @Builder
    public static class PlayerSummary {
        private String id;
        private String username;
    }
}
