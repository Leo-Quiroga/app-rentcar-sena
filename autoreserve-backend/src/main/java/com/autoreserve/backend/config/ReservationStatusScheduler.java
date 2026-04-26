package com.autoreserve.backend.config;

import com.autoreserve.backend.domain.entity.*;
import com.autoreserve.backend.domain.repository.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class ReservationStatusScheduler {

    private final ReservationRepository reservationRepository;
    private final CarRepository carRepository;

    public ReservationStatusScheduler(ReservationRepository reservationRepository,
                                      CarRepository carRepository) {
        this.reservationRepository = reservationRepository;
        this.carRepository = carRepository;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void updateReservationStatuses() {
        LocalDate today = LocalDate.now();
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);

        // 1. Cancelar reservas PENDING con más de 24h sin pagar
        List<Reservation> expiredPending = reservationRepository.findExpiredPendingReservations(cutoff);
        for (Reservation r : expiredPending) {
            r.setStatus(ReservationStatus.CANCELLED);
            // No hay auto asignado en PENDING, no hay nada que liberar
        }
        if (!expiredPending.isEmpty()) reservationRepository.saveAll(expiredPending);

        // 2. Cancelar reservas PENDING que llegaron a su startDate sin auto disponible
        List<Reservation> pendingAtStart = reservationRepository.findPendingWithoutCarReachedStartDate(today);
        for (Reservation r : pendingAtStart) {
            r.setStatus(ReservationStatus.CANCELLED);
        }
        if (!pendingAtStart.isEmpty()) reservationRepository.saveAll(pendingAtStart);

        // 3. Pasar a IN_PROGRESS las reservas CONFIRMED cuyo startDate llegó
        List<Reservation> startingToday = reservationRepository.findConfirmedStartingToday(today);
        for (Reservation r : startingToday) {
            r.setStatus(ReservationStatus.IN_PROGRESS);
            // El auto ya está RENTED desde que se confirmó el pago
        }
        if (!startingToday.isEmpty()) reservationRepository.saveAll(startingToday);

        // 4. Pasar a COMPLETED las reservas IN_PROGRESS cuyo endDate ya pasó y liberar el auto
        List<Reservation> ended = reservationRepository.findInProgressEndedBeforeToday(today);
        for (Reservation r : ended) {
            r.setStatus(ReservationStatus.COMPLETED);
            if (r.getCar() != null) {
                r.getCar().setStatus(CarStatus.AVAILABLE);
                carRepository.save(r.getCar());
            }
        }
        if (!ended.isEmpty()) reservationRepository.saveAll(ended);
    }
}
