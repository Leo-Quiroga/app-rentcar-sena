package com.autoreserve.backend.domain.repository;

import com.autoreserve.backend.domain.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
