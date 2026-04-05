package com.gameboost.backend.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data @Builder
public class ReviewResponse {
    private Long id;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
