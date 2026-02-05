package com.autoreserve.backend.web.controller;

import com.autoreserve.backend.domain.entity.Car;
import com.autoreserve.backend.domain.repository.CarRepository;
import com.autoreserve.backend.dto.car.CarResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final CarRepository carRepository;

    public SearchController(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @GetMapping("/cars")
    public ResponseEntity<List<CarResponse>> searchAvailableCars(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long categoryId) {
        
        List<Car> availableCars = carRepository.findAvailableCars(startDate, endDate, categoryId);
        
        List<CarResponse> responses = availableCars.stream()
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
                        car.getImage()
                ))
                .toList();
        
        return ResponseEntity.ok(responses);
    }
}