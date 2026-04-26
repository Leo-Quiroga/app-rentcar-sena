package com.autoreserve.backend.web.controller;

import com.autoreserve.backend.domain.entity.CarModel;
import com.autoreserve.backend.domain.repository.CarModelRepository;
import com.autoreserve.backend.domain.repository.CarRepository;
import com.autoreserve.backend.dto.car.CarModelResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final CarModelRepository carModelRepository;
    private final CarRepository carRepository;

    public SearchController(CarModelRepository carModelRepository, CarRepository carRepository) {
        this.carModelRepository = carModelRepository;
        this.carRepository = carRepository;
    }

    @GetMapping("/cars")
    public ResponseEntity<?> searchAvailableModels(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long categoryId) {

        if (endDate.isBefore(startDate)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error",
                            "La fecha de fin debe ser igual o posterior a la fecha de inicio"));
        }

        List<CarModel> models = carModelRepository.findAvailableModels(startDate, endDate, categoryId);

        List<CarModelResponse> responses = models.stream().map(m -> {
            // Contar unidades disponibles para esas fechas específicas
            long available = carRepository.findAvailableUnitForModel(m.getId(), startDate, endDate).size();
            long total = carRepository.countByCarModelId(m.getId());
            return new CarModelResponse(
                    m.getId(), m.getBrand(), m.getModel(), m.getYear(),
                    m.getPricePerDay(), m.getImage(), m.getDescription(),
                    m.getCategory().getName(), m.getCategory().getId(),
                    available, total
            );
        }).toList();

        return ResponseEntity.ok(responses);
    }
}
