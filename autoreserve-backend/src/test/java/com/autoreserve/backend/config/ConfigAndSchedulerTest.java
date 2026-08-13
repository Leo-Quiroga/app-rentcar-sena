package com.autoreserve.backend.config;

import com.autoreserve.backend.domain.entity.Car;
import com.autoreserve.backend.domain.entity.CarStatus;
import com.autoreserve.backend.domain.entity.Reservation;
import com.autoreserve.backend.domain.entity.ReservationStatus;
import com.autoreserve.backend.AutoreserveBackendApplication;
import com.autoreserve.backend.domain.repository.BranchRepository;
import com.autoreserve.backend.domain.repository.CarModelRepository;
import com.autoreserve.backend.domain.repository.CarRepository;
import com.autoreserve.backend.domain.repository.CategoryRepository;
import com.autoreserve.backend.domain.repository.ReservationRepository;
import com.autoreserve.backend.domain.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfigAndSchedulerTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private CarRepository carRepository;

    @Test
    void corsConfigurationSourceAllowsFrontendOrigins() {
        CorsConfig config = new CorsConfig();
        var source = config.corsConfigurationSource();
        assertThat(source).isNotNull();
        var corsConfiguration = source.getCorsConfiguration(new MockHttpServletRequest());
        assertThat(corsConfiguration.getAllowedOrigins()).containsExactlyInAnyOrder("http://localhost:5173", "http://localhost:3000");
        assertThat(corsConfiguration.getAllowedMethods()).contains("GET", "POST", "PUT", "DELETE", "OPTIONS");
        assertThat(corsConfiguration.getAllowCredentials()).isTrue();
    }

    @Test
    void handleAccessDeniedReturnsForbiddenStatus() {
        GlobalSecurityExceptionHandler handler = new GlobalSecurityExceptionHandler();
        ResponseEntity<String> response = handler.handleAccessDenied(new AccessDeniedException("denied"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isEqualTo("Forbidden");
    }

    @Test
    void reservationStatusSchedulerProcessesReservationTransitions() {
        ReservationStatusScheduler scheduler = new ReservationStatusScheduler(reservationRepository, carRepository);

        Reservation expiredPending = new Reservation();
        expiredPending.setId(10L);
        expiredPending.setStatus(ReservationStatus.PENDING);

        Reservation pendingAtStart = new Reservation();
        pendingAtStart.setId(11L);
        pendingAtStart.setStatus(ReservationStatus.PENDING);

        Reservation startingToday = new Reservation();
        startingToday.setId(12L);
        startingToday.setStatus(ReservationStatus.CONFIRMED);
        Car startingCar = new Car();
        startingCar.setId(100L);
        startingCar.setPlate("AAA-111");
        startingToday.setCar(startingCar);

        Reservation ended = new Reservation();
        ended.setId(13L);
        ended.setStatus(ReservationStatus.IN_PROGRESS);
        Car endedCar = new Car();
        endedCar.setId(101L);
        endedCar.setPlate("BBB-222");
        ended.setCar(endedCar);

        when(reservationRepository.findExpiredPendingReservations(any())).thenReturn(List.of(expiredPending));
        when(reservationRepository.findPendingWithoutCarReachedStartDate(any())).thenReturn(List.of(pendingAtStart));
        when(reservationRepository.findConfirmedStartingToday(any())).thenReturn(List.of(startingToday));
        when(reservationRepository.findInProgressEndedBeforeToday(any())).thenReturn(List.of(ended));

        scheduler.updateReservationStatuses();

        assertThat(expiredPending.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
        assertThat(pendingAtStart.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
        assertThat(startingToday.getStatus()).isEqualTo(ReservationStatus.IN_PROGRESS);
        assertThat(startingCar.getStatus()).isEqualTo(CarStatus.RENTED);
        assertThat(ended.getStatus()).isEqualTo(ReservationStatus.COMPLETED);
        assertThat(endedCar.getStatus()).isEqualTo(CarStatus.AVAILABLE);

        verify(carRepository, times(2)).save(any(Car.class));
        verify(reservationRepository, times(4)).saveAll(any());
    }

    @Test
    void dataInitializerSavesInitialDataWhenStoreIsEmpty() throws Exception {
        DataInitializer initializer = new DataInitializer();
        var roleRepository = mock(RoleRepository.class);
        var carRepository = mock(CarRepository.class);
        var categoryRepository = mock(CategoryRepository.class);
        var branchRepository = mock(BranchRepository.class);
        var carModelRepository = mock(CarModelRepository.class);

        setField(initializer, "roleRepository", roleRepository);
        setField(initializer, "carRepository", carRepository);
        setField(initializer, "categoryRepository", categoryRepository);
        setField(initializer, "branchRepository", branchRepository);
        setField(initializer, "carModelRepository", carModelRepository);

        when(categoryRepository.count()).thenReturn(1L);
        when(branchRepository.count()).thenReturn(0L);
        when(carRepository.count()).thenReturn(0L);
        when(carModelRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(carRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        initializer.run();

        verify(categoryRepository, times(1)).save(any());
        verify(branchRepository, times(1)).save(any());
        verify(carModelRepository, times(1)).save(any());
        verify(carRepository, times(1)).save(any());
    }

    @Test
    void applicationMainCallsSpringApplicationRun() {
        try (var mocked = mockStatic(SpringApplication.class)) {
            mocked.when(() -> SpringApplication.run(AutoreserveBackendApplication.class, new String[0]))
                    .thenReturn(mock(ConfigurableApplicationContext.class));
            AutoreserveBackendApplication.main(new String[0]);
            mocked.verify(() -> SpringApplication.run(AutoreserveBackendApplication.class, new String[0]));
        }
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }
}
