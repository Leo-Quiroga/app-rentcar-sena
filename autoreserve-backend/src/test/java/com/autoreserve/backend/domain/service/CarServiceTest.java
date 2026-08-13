package com.autoreserve.backend.domain.service;

import com.autoreserve.backend.domain.entity.Car;
import com.autoreserve.backend.domain.entity.CarStatus;
import com.autoreserve.backend.domain.repository.CarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private CarService carService;

    private Car testCar;

    @BeforeEach
    void setUp() {
        testCar = new Car();
        testCar.setId(1L);
        testCar.setPlate("ABC123");
        testCar.setStatus(CarStatus.AVAILABLE);
    }

    @Test
    void save_ReturnsPersistedCar() {
        when(carRepository.save(any(Car.class))).thenReturn(testCar);

        Car result = carService.save(testCar);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getPlate()).isEqualTo("ABC123");
        verify(carRepository).save(testCar);
    }

    @Test
    void findAll_ReturnsAllCars() {
        when(carRepository.findAll()).thenReturn(List.of(testCar));

        List<Car> result = carService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(CarStatus.AVAILABLE);
    }

    @Test
    void findById_ExistingId_ReturnsCar() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));

        Optional<Car> result = carService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getPlate()).isEqualTo("ABC123");
    }

    @Test
    void findById_NonExistingId_ReturnsEmpty() {
        when(carRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Car> result = carService.findById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void deleteById_CallsRepository() {
        carService.deleteById(1L);

        verify(carRepository).deleteById(1L);
    }

    @Test
    void findAllByStatus_ReturnsCarsByStatus() {
        when(carRepository.findByStatus(CarStatus.AVAILABLE)).thenReturn(List.of(testCar));

        List<Car> result = carService.findAllByStatus(CarStatus.AVAILABLE);

        assertThat(result).hasSize(1);
        verify(carRepository).findByStatus(CarStatus.AVAILABLE);
    }

    @Test
    void findAvailableUnitForModel_ReturnsAvailableUnit() {
        when(carRepository.findAvailableUnitForModel(1L, null, null)).thenReturn(List.of(testCar));

        List<Car> result = carService.findAvailableUnitForModel(1L, null, null);

        assertThat(result).hasSize(1);
        verify(carRepository).findAvailableUnitForModel(1L, null, null);
    }

    @Test
    void countAvailableByModel_ReturnsCount() {
        when(carRepository.countAvailableByModel(1L)).thenReturn(5L);

        Long result = carService.countAvailableByModel(1L);

        assertThat(result).isEqualTo(5L);
        verify(carRepository).countAvailableByModel(1L);
    }

    @Test
    void countByCarModelId_ReturnsCount() {
        when(carRepository.countByCarModelId(1L)).thenReturn(10L);

        Long result = carService.countByCarModelId(1L);

        assertThat(result).isEqualTo(10L);
        verify(carRepository).countByCarModelId(1L);
    }
}
