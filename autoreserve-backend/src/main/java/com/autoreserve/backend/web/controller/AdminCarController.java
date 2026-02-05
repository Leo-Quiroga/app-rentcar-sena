package com.autoreserve.backend.web.controller;

import com.autoreserve.backend.domain.entity.*;
import com.autoreserve.backend.domain.repository.*;
import com.autoreserve.backend.dto.car.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/cars")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCarController {

    private final CarRepository carRepository;
    private final CategoryRepository categoryRepository;
    private final BranchRepository branchRepository;

    public AdminCarController(CarRepository carRepository, CategoryRepository categoryRepository, BranchRepository branchRepository) {
        this.carRepository = carRepository;
        this.categoryRepository = categoryRepository;
        this.branchRepository = branchRepository;
    }

    @GetMapping
    public ResponseEntity<List<CarResponse>> getAllCars(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<Car> carPage = carRepository.findAll(PageRequest.of(page, size));
        
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
                        car.getImage()
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
                car.getImage()
        );
        
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> createCar(@Valid @RequestBody CarRequest request) {
        try {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + request.getCategoryId()));
            
            Branch branch = branchRepository.findById(request.getBranchId())
                    .orElseThrow(() -> new RuntimeException("Sede no encontrada con ID: " + request.getBranchId()));

            Car car = new Car();
            car.setBrand(request.getBrand());
            car.setModel(request.getModel());
            car.setYear(request.getYear());
            car.setPlate(request.getPlate());
            car.setPricePerDay(request.getPricePerDay());
            car.setStatus(CarStatus.AVAILABLE);
            car.setCategory(category);
            car.setBranch(branch);
            car.setImage(request.getImage());

            Car saved = carRepository.save(car);

            CarResponse response = new CarResponse(
                    saved.getId(),
                    saved.getBrand(),
                    saved.getModel(),
                    saved.getYear(),
                    saved.getPlate(),
                    saved.getPricePerDay(),
                    saved.getStatus().name(),
                    saved.getCategory().getName(),
                    saved.getBranch().getName(),
                    saved.getImage()
            );

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Auto creado exitosamente",
                    "data", response
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", "Error interno del servidor", "details", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarResponse> updateCar(@PathVariable Long id, @Valid @RequestBody CarRequest request) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Auto no encontrado"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        
        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new RuntimeException("Sede no encontrada"));

        car.setBrand(request.getBrand());
        car.setModel(request.getModel());
        car.setYear(request.getYear());
        car.setPlate(request.getPlate());
        car.setPricePerDay(request.getPricePerDay());
        car.setCategory(category);
        car.setBranch(branch);
        car.setImage(request.getImage());

        Car updated = carRepository.save(car);

        CarResponse response = new CarResponse(
                updated.getId(),
                updated.getBrand(),
                updated.getModel(),
                updated.getYear(),
                updated.getPlate(),
                updated.getPricePerDay(),
                updated.getStatus().name(),
                updated.getCategory().getName(),
                updated.getBranch().getName(),
                updated.getImage()
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCar(@PathVariable Long id) {
        try {
            Car car = carRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Auto no encontrado con ID: " + id));

            carRepository.delete(car);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Auto eliminado exitosamente",
                    "deletedId", id
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", "Error interno del servidor", "details", e.getMessage()));
        }
    }
}