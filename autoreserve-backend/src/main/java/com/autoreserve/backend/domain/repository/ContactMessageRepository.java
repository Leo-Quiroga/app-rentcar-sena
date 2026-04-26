package com.autoreserve.backend.domain.repository;

import com.autoreserve.backend.domain.entity.ContactMessage;
import com.autoreserve.backend.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {
    List<ContactMessage> findByUserOrderByCreatedAtDesc(User user);
    List<ContactMessage> findAllByOrderByCreatedAtDesc();
}
