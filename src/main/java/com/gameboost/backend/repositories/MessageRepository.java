package com.gameboost.backend.repositories;

import com.gameboost.backend.models.Message;
import com.gameboost.backend.models.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySessionOrderBySentAtAsc(Session session);
    long countBySession(Session session);
}
