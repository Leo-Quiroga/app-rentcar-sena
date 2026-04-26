package com.autoreserve.backend.web.controller;

import com.autoreserve.backend.domain.entity.CarModel;
import com.autoreserve.backend.domain.repository.CarModelRepository;
import com.autoreserve.backend.domain.repository.CarRepository;
import com.autoreserve.backend.dto.car.CarModelResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cars")
public class CarController {

    private final CarModelRepository carModelRepository;
    private final CarRepository carRepository;

    public CarController(CarModelRepository carModelRepository, CarRepository carRepository) {
        this.carModelRepository = carModelRepository;
        this.carRepository = carRepository;
    }

    /** Catálogo público: modelos con al menos 1 unidad disponible */
    @GetMapping
    public ResponseEntity<List<CarModelResponse>> getAvailableModels(
            @RequestParam(required = false) Long categoryId) {

        List<CarModel> models = carModelRepository.findModelsWithAvailableUnits(categoryId);
        List<CarModelResponse> response = toResponseList(models);
        return ResponseEntity.ok(response);
    }

    /** Buscar modelos disponibles en fechas específicas */
    @GetMapping("/available")
    public ResponseEntity<List<CarModelResponse>> getAvailableModelsForDates(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(required = false) Long categoryId) {

        List<CarModel> models = carModelRepository.findAvailableModels(startDate, endDate, categoryId);
        List<CarModelResponse> response = toResponseList(models);
        return ResponseEntity.ok(response);
    }

    /** Detalle de un modelo por ID */
    @GetMapping("/{id}")
    public ResponseEntity<?> getModelById(@PathVariable Long id) {
        CarModel model = carModelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Modelo no encontrado"));

        long available = carRepository.countAvailableByModel(id);
        long total = carRepository.countByCarModelId(id);

        CarModelResponse response = toResponse(model, available, total);
        return ResponseEntity.ok(response);
    }

    // Helper
    private List<CarModelResponse> toResponseList(List<CarModel> models) {
        return models.stream().map(m -> {
            long available = carRepository.countAvailableByModel(m.getId());
            long total = carRepository.countByCarModelId(m.getId());
            return toResponse(m, available, total);
        }).toList();
    }

    private CarModelResponse toResponse(CarModel m, long available, long total) {
        return new CarModelResponse(
                m.getId(),
                m.getBrand(),
                m.getModel(),
                m.getYear(),
                m.getPricePerDay(),
                m.getImage(),
                m.getDescription(),
                m.getCategory().getName(),
                m.getCategory().getId(),
                available,
                total
        );
    }
}
