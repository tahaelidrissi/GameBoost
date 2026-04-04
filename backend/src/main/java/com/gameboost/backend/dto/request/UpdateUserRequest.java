package com.gameboost.backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UpdateUserRequest {
    @Email
    private String email;
    @Size(min = 3, max = 20)
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username : lettres, chiffres et underscore uniquement")
    private String username;
}
