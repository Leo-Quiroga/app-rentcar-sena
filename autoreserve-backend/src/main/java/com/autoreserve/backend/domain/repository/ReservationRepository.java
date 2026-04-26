package com.autoreserve.backend.domain.repository;

import com.autoreserve.backend.domain.entity.Reservation;
import com.autoreserve.backend.domain.entity.ReservationStatus;
import com.autoreserve.backend.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserOrderByStartDateDesc(User user);

    // Detecta solapamiento solo con reservas CONFIRMED o IN_PROGRESS
    @Query("SELECT COUNT(r) > 0 FROM Reservation r WHERE r.car.id = :carId " +
           "AND (r.status = 'CONFIRMED' OR r.status = 'IN_PROGRESS') " +
           "AND (:startDate <= r.endDate) AND (:endDate >= r.startDate)")
    boolean existsOverlappingReservation(@Param("carId") Long carId,
                                         @Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);

    // Reservas PENDING con más de 24 horas sin pagar (para cancelación automática)
    @Query("SELECT r FROM Reservation r WHERE r.status = 'PENDING' AND r.createdAt <= :cutoff")
    List<Reservation> findExpiredPendingReservations(@Param("cutoff") LocalDateTime cutoff);

    // Reservas CONFIRMED cuyo startDate ya llegó (para pasar a IN_PROGRESS)
    @Query("SELECT r FROM Reservation r WHERE r.status = 'CONFIRMED' AND r.startDate <= :today")
    List<Reservation> findConfirmedStartingToday(@Param("today") LocalDate today);

    // Reservas IN_PROGRESS cuyo endDate ya pasó (para pasar a COMPLETED)
    @Query("SELECT r FROM Reservation r WHERE r.status = 'IN_PROGRESS' AND r.endDate < :today")
    List<Reservation> findInProgressEndedBeforeToday(@Param("today") LocalDate today);
}
