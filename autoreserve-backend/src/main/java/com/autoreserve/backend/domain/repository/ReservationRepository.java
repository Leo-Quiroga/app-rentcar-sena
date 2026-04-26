package com.autoreserve.backend.domain.repository;

import com.autoreserve.backend.domain.entity.Reservation;
import com.autoreserve.backend.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUserOrderByStartDateDesc(User user);

    // Solapamiento solo con reservas CONFIRMED o IN_PROGRESS (tienen auto asignado y pagado)
    @Query("SELECT COUNT(r) > 0 FROM Reservation r WHERE r.car.id = :carId " +
           "AND (r.status = 'CONFIRMED' OR r.status = 'IN_PROGRESS') " +
           "AND (:startDate <= r.endDate) AND (:endDate >= r.startDate)")
    boolean existsOverlappingReservation(@Param("carId") Long carId,
                                         @Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);

    // Reservas PENDING expiradas (más de 24h sin pagar)
    @Query("SELECT r FROM Reservation r WHERE r.status = 'PENDING' AND r.createdAt <= :cutoff")
    List<Reservation> findExpiredPendingReservations(@Param("cutoff") LocalDateTime cutoff);

    // Reservas PENDING sin auto disponible que llegaron a su startDate
    @Query("SELECT r FROM Reservation r WHERE r.status = 'PENDING' AND r.car IS NULL AND r.startDate <= :today")
    List<Reservation> findPendingWithoutCarReachedStartDate(@Param("today") LocalDate today);

    // Reservas CONFIRMED cuyo startDate llegó
    @Query("SELECT r FROM Reservation r WHERE r.status = 'CONFIRMED' AND r.startDate <= :today")
    List<Reservation> findConfirmedStartingToday(@Param("today") LocalDate today);

    // Reservas IN_PROGRESS cuyo endDate ya pasó
    @Query("SELECT r FROM Reservation r WHERE r.status = 'IN_PROGRESS' AND r.endDate < :today")
    List<Reservation> findInProgressEndedBeforeToday(@Param("today") LocalDate today);
}
