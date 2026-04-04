package com.gameboost.backend.services;

import com.gameboost.backend.dto.request.MessageRequest;
import com.gameboost.backend.dto.response.MessageResponse;
import com.gameboost.backend.models.*;
import com.gameboost.backend.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;

    public List<MessageResponse> getMessages(String email, Long sessionId) {
        Session session = getSession(sessionId);
        assertAccess(email, session);
        return messageRepository.findBySessionOrderBySentAtAsc(session).stream()
                .map(m -> MessageResponse.builder().id(m.getId())
                        .senderUsername(m.getSender().getUsername())
                        .content(m.getContent()).sentAt(m.getSentAt()).build())
                .collect(Collectors.toList());
    }

    public Message sendMessage(String email, Long sessionId, MessageRequest request) {
        Session session = getSession(sessionId);
        User sender = assertAccess(email, session);

        if (session.getStatus() == Session.Status.REQUESTED)
            throw new IllegalArgumentException("Le chat n'est disponible qu'après acceptation de la session");

        Message message = Message.builder().session(session).sender(sender)
                .content(request.getContent()).build();
        return messageRepository.save(message);
    }

    private User assertAccess(String email, Session session) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));
        boolean isPlayer = session.getPlayer().getEmail().equals(email);
        boolean isCoach = session.getCoach().getUser().getEmail().equals(email);
        if (!isPlayer && !isCoach)
            throw new AccessDeniedException("Vous n'avez pas accès à cette conversation");
        return user;
    }

    private Session getSession(Long sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session non trouvée"));
    }
}
