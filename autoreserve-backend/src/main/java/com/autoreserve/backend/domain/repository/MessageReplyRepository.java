package com.autoreserve.backend.domain.repository;

import com.autoreserve.backend.domain.entity.MessageReply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageReplyRepository extends JpaRepository<MessageReply, Long> {
    List<MessageReply> findByContactMessageIdOrderByCreatedAtAsc(Long contactMessageId);
}
