package com.autoreserve.backend.domain.repository;

import com.autoreserve.backend.domain.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
