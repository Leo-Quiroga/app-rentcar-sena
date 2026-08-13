package com.autoreserve.backend.domain.service;

import com.autoreserve.backend.domain.entity.Reservation;
import com.autoreserve.backend.domain.entity.ReservationStatus;
import com.autoreserve.backend.domain.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationService reservationService;

    private Reservation testReservation;

    @BeforeEach
    void setUp() {
        testReservation = new Reservation();
        testReservation.setId(1L);
        testReservation.setStatus(ReservationStatus.PENDING);
    }

    @Test
    void save_ReturnsPersistedReservation() {
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);

        Reservation result = reservationService.save(testReservation);

        assertThat(result.getId()).isEqualTo(1L);
        verify(reservationRepository).save(testReservation);
    }

    @Test
    void findAll_ReturnsAllReservations() {
        when(reservationRepository.findAll()).thenReturn(List.of(testReservation));

        List<Reservation> result = reservationService.findAll();

        assertThat(result).hasSize(1);
    }

    @Test
    void findById_ExistingId_ReturnsReservation() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));

        Optional<Reservation> result = reservationService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
    }

    @Test
    void findById_NonExistingId_ReturnsEmpty() {
        when(reservationRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Reservation> result = reservationService.findById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void deleteById_CallsRepository() {
        reservationService.deleteById(1L);

        verify(reservationRepository).deleteById(1L);
    }
}
