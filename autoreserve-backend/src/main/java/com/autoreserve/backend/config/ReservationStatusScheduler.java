package com.autoreserve.backend.config;

import com.autoreserve.backend.domain.entity.Reservation;
import com.autoreserve.backend.domain.entity.ReservationStatus;
import com.autoreserve.backend.domain.repository.ReservationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduler que actualiza automáticamente los estados de las reservas una vez al día.
 *
 * Reglas:
 * - PENDING con más de 24h sin pagar → CANCELLED
 * - CONFIRMED cuyo startDate llegó → IN_PROGRESS
 * - IN_PROGRESS cuyo endDate ya pasó → COMPLETED
 */
@Component
public class ReservationStatusScheduler {

    private final ReservationRepository reservationRepository;

    public ReservationStatusScheduler(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    // Se ejecuta todos los días a medianoche
    @Scheduled(cron = "0 0 0 * * *")
    public void updateReservationStatuses() {
        LocalDate today = LocalDate.now();
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);

        // 1. Cancelar reservas PENDING con más de 24 horas sin pagar
        List<Reservation> expiredPending = reservationRepository.findExpiredPendingReservations(cutoff);
        for (Reservation r : expiredPending) {
            r.setStatus(ReservationStatus.CANCELLED);
        }
        if (!expiredPending.isEmpty()) {
            reservationRepository.saveAll(expiredPending);
        }

        // 2. Pasar a IN_PROGRESS las reservas CONFIRMED cuyo startDate ya llegó
        List<Reservation> startingToday = reservationRepository.findConfirmedStartingToday(today);
        for (Reservation r : startingToday) {
            r.setStatus(ReservationStatus.IN_PROGRESS);
        }
        if (!startingToday.isEmpty()) {
            reservationRepository.saveAll(startingToday);
        }

        // 3. Pasar a COMPLETED las reservas IN_PROGRESS cuyo endDate ya pasó
        List<Reservation> ended = reservationRepository.findInProgressEndedBeforeToday(today);
        for (Reservation r : ended) {
            r.setStatus(ReservationStatus.COMPLETED);
        }
        if (!ended.isEmpty()) {
            reservationRepository.saveAll(ended);
        }
    }
}
