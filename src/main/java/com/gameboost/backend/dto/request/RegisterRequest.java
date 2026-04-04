package com.gameboost.backend.dto.request;

import com.gameboost.backend.models.User;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank @Email
    private String email;

    @NotBlank
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).+$",
             message = "Le mot de passe doit contenir au moins une majuscule et un chiffre")
    private String password;

    @NotBlank @Size(min = 3, max = 20)
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username : lettres, chiffres et underscore uniquement")
    private String username;

    @NotNull
    private User.Role role;
}
