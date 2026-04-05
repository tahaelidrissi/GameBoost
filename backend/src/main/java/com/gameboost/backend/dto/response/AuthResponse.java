package com.gameboost.backend.dto.response;

import com.gameboost.backend.models.User;
import lombok.*;
import java.util.UUID;

@Data @Builder
public class AuthResponse {
    private String token;
    private UUID id;
    private String email;
    private String username;
    private User.Role role;
    private Boolean isApproved;
}
