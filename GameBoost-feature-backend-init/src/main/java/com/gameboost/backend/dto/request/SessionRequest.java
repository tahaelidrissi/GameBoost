package com.gameboost.backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SessionRequest {
    @NotNull
    private Long coachId;
    @NotNull @Min(1) @Max(24)
    private Integer durationHours;
    @NotNull
    @Future(message = "La date doit être dans le futur")
    private LocalDateTime scheduledAt;
}
