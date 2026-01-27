package com.autoreserve.backend.domain.repository;

import com.autoreserve.backend.domain.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
