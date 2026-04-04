package com.gameboost.backend.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data @Builder
public class MessageResponse {
    private Long id;
    private String senderUsername;
    private String content;
    private LocalDateTime sentAt;
}
