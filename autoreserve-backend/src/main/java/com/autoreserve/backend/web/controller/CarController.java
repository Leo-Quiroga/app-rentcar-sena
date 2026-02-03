package com.autoreserve.backend.web.controller;

import com.autoreserve.backend.domain.entity.Car;
import com.autoreserve.backend.domain.entity.CarStatus;
import com.autoreserve.backend.domain.repository.CarRepository;
import com.autoreserve.backend.dto.car.CarResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
public class CarController {

    private final CarRepository carRepository;

    public CarController(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @GetMapping
    public ResponseEntity<List<CarResponse>> getAvailableCars(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long branchId) {
        
        Page<Car> carPage;
        
        if (categoryId != null && branchId != null) {
            carPage = carRepository.findByCategoryIdAndBranchIdAndStatus(
                    categoryId, branchId, CarStatus.AVAILABLE, PageRequest.of(page, size));
        } else if (categoryId != null) {
            carPage = carRepository.findByCategoryIdAndStatus(
                    categoryId, CarStatus.AVAILABLE, PageRequest.of(page, size));
        } else if (branchId != null) {
            carPage = carRepository.findByBranchIdAndStatus(
                    branchId, CarStatus.AVAILABLE, PageRequest.of(page, size));
        } else {
            carPage = carRepository.findByStatus(CarStatus.AVAILABLE, PageRequest.of(page, size));
        }
        
        List<CarResponse> cars = carPage.getContent().stream()
                .map(car -> new CarResponse(
                        car.getId(),
                        car.getBrand(),
                        car.getModel(),
                        car.getYear(),
                        car.getPlate(),
                        car.getPricePerDay(),
                        car.getStatus().name(),
                        car.getCategory().getName(),
                        car.getBranch().getName(),
                        null
                ))
                .toList();
        
        return ResponseEntity.ok(cars);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarResponse> getCarById(@PathVariable Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Auto no encontrado"));
        
        CarResponse response = new CarResponse(
                car.getId(),
                car.getBrand(),
                car.getModel(),
                car.getYear(),
                car.getPlate(),
                car.getPricePerDay(),
                car.getStatus().name(),
                car.getCategory().getName(),
                car.getBranch().getName(),
                null
        );
        
        return ResponseEntity.ok(response);
    }
}