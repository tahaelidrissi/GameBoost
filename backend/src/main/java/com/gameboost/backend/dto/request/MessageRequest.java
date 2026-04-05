package com.gameboost.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MessageRequest {
    @NotBlank(message = "Le message ne peut pas être vide")
    private String content;
}
