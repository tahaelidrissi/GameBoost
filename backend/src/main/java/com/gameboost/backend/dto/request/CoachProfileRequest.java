package com.gameboost.backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CoachProfileRequest {
    @NotBlank
    private String gameTitle;
    @NotBlank
    private String rank;
    @Size(max = 500)
    private String bio;
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Digits(integer = 8, fraction = 2)
    private BigDecimal hourlyRate;
    private String proofImage;
}
