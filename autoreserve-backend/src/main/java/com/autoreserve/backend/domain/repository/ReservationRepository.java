package com.autoreserve.backend.domain.repository;

import com.autoreserve.backend.domain.entity.Reservation;
import com.autoreserve.backend.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserOrderByStartDateDesc(User user);
}
