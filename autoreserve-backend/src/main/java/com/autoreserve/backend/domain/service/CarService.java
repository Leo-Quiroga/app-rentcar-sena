package com.autoreserve.backend.domain.service;

import com.autoreserve.backend.domain.entity.Car;
import com.autoreserve.backend.domain.entity.CarStatus;
import com.autoreserve.backend.domain.repository.CarRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

@Service
public class CarService {

    private final CarRepository carRepository;

    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public Car save(Car car) {
        return carRepository.save(car);
    }

    public List<Car> findAll() {
        return carRepository.findAll();
    }

    public Optional<Car> findById(Long id) {
        return carRepository.findById(id);
    }

    public void deleteById(Long id) {
        carRepository.deleteById(id);
    }

    public List<Car> findAllByStatus(CarStatus status) {
        return carRepository.findByStatus(status);
    }

    public List<Car> findAvailableUnitForModel(Long carModelId, LocalDate startDate, LocalDate endDate) {
        return carRepository.findAvailableUnitForModel(carModelId, startDate, endDate);
    }

    public Long countAvailableByModel(Long carModelId) {
        return carRepository.countAvailableByModel(carModelId);
    }

    public Long countByCarModelId(Long carModelId) {
        return carRepository.countByCarModelId(carModelId);
    }
}
