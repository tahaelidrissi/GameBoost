package com.gameboost.backend.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data @Builder
public class CoachResponse {
    private Long id;
    private String username;
    private String email;
    private String gameTitle;
    private String rank;
    private String bio;
    private BigDecimal hourlyRate;
    private Double averageRating;
    private Long totalReviews;
    private List<ReviewResponse> reviews;
}
