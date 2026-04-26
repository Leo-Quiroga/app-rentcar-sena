package com.autoreserve.backend.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autoreserve.backend.domain.entity.Branch;
import com.autoreserve.backend.domain.entity.Car;
import com.autoreserve.backend.domain.entity.CarModel;
import com.autoreserve.backend.domain.entity.CarStatus;
import com.autoreserve.backend.domain.entity.Category;
import com.autoreserve.backend.domain.repository.BranchRepository;
import com.autoreserve.backend.domain.repository.CarModelRepository;
import com.autoreserve.backend.domain.repository.CarRepository;
import com.autoreserve.backend.domain.repository.CategoryRepository;
import com.autoreserve.backend.dto.car.CarModelRequest;
import com.autoreserve.backend.dto.car.CarUnitResponse;
import com.autoreserve.backend.dto.car.CarUnitUpdateRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/cars")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCarController {

    private final CarRepository carRepository;
    private final CarModelRepository carModelRepository;
    private final CategoryRepository categoryRepository;
    private final BranchRepository branchRepository;

    public AdminCarController(CarRepository carRepository,
                              CarModelRepository carModelRepository,
                              CategoryRepository categoryRepository,
                              BranchRepository branchRepository) {
        this.carRepository = carRepository;
        this.carModelRepository = carModelRepository;
        this.categoryRepository = categoryRepository;
        this.branchRepository = branchRepository;
    }

    // ===================== MODELOS =====================

    /** Lista todos los modelos con conteo de unidades */
    @GetMapping("/models")
    public ResponseEntity<?> getAllModels() {
        List<CarModel> models = carModelRepository.findAll();
        List<Map<String, Object>> response = models.stream().map(m -> {
            long available = carRepository.countAvailableByModel(m.getId());
            long total = carRepository.countByCarModelId(m.getId());
            Map<String, Object> map = new HashMap<>();
            map.put("id", m.getId());
            map.put("brand", m.getBrand());
            map.put("model", m.getModel());
            map.put("year", m.getYear());
            map.put("pricePerDay", m.getPricePerDay());
            map.put("image", m.getImage() != null ? m.getImage() : "");
            map.put("description", m.getDescription() != null ? m.getDescription() : "");
            map.put("categoryName", m.getCategory().getName());
            map.put("categoryId", m.getCategory().getId());
            map.put("availableUnits", available);
            map.put("totalUnits", total);
            return map;
        }).toList();
        return ResponseEntity.ok(response);
    }

    /** Crea un modelo y genera N unidades en PENDING_REGISTRATION */
    @PostMapping("/models")
    public ResponseEntity<?> createModel(@Valid @RequestBody CarModelRequest request) {
        try {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
            Branch branch = branchRepository.findById(request.getBranchId())
                    .orElseThrow(() -> new RuntimeException("Sede no encontrada"));

            CarModel carModel = new CarModel();
            carModel.setBrand(request.getBrand());
            carModel.setModel(request.getModel());
            carModel.setYear(request.getYear());
            carModel.setPricePerDay(request.getPricePerDay());
            carModel.setImage(request.getImage());
            carModel.setDescription(request.getDescription());
            carModel.setCategory(category);
            CarModel saved = carModelRepository.save(carModel);

            // Crear N unidades en PENDING_REGISTRATION
            for (int i = 0; i < request.getQuantity(); i++) {
                Car unit = new Car();
                unit.setCarModel(saved);
                unit.setBranch(branch);
                unit.setStatus(CarStatus.PENDING_REGISTRATION);
                carRepository.save(unit);
            }

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Modelo creado con " + request.getQuantity() + " unidad(es) pendiente(s) de identificación",
                "modelId", saved.getId(),
                "unitsCreated", request.getQuantity()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /** Actualiza datos del modelo (precio, imagen, descripción) */
    @PutMapping("/models/{id}")
    public ResponseEntity<?> updateModel(@PathVariable Long id, @RequestBody CarModelRequest request) {
        try {
            CarModel model = carModelRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Modelo no encontrado"));
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

            model.setBrand(request.getBrand());
            model.setModel(request.getModel());
            model.setYear(request.getYear());
            model.setPricePerDay(request.getPricePerDay());
            model.setImage(request.getImage());
            model.setDescription(request.getDescription());
            model.setCategory(category);
            carModelRepository.save(model);

            return ResponseEntity.ok(Map.of("success", true, "message", "Modelo actualizado exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /** Elimina un modelo solo si no tiene unidades con reservas activas */
    @DeleteMapping("/models/{id}")
    public ResponseEntity<?> deleteModel(@PathVariable Long id) {
        try {
            CarModel model = carModelRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Modelo no encontrado"));
            carModelRepository.delete(model);
            return ResponseEntity.ok(Map.of("success", true, "message", "Modelo eliminado exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    // ===================== UNIDADES =====================

    /** Lista todas las unidades de un modelo */
    @GetMapping("/models/{modelId}/units")
    public ResponseEntity<?> getUnitsByModel(@PathVariable Long modelId) {
        List<Car> units = carRepository.findByCarModelId(modelId);
        List<CarUnitResponse> response = units.stream().map(u -> new CarUnitResponse(
                u.getId(),
                u.getCarModel().getId(),
                u.getCarModel().getBrand(),
                u.getCarModel().getModel(),
                u.getCarModel().getYear(),
                u.getPlate(),
                u.getColor(),
                u.getStatus().name(),
                u.getBranch().getName(),
                u.getBranch().getId(),
                u.getNotes()
        )).toList();
        return ResponseEntity.ok(response);
    }

    /** Actualiza una unidad: placa, color, estado, sede, notas.
     *  Regla: solo se puede poner AVAILABLE si tiene placa Y color. */
    @PutMapping("/units/{unitId}")
    public ResponseEntity<?> updateUnit(@PathVariable Long unitId,
                                        @RequestBody CarUnitUpdateRequest request) {
        try {
            Car unit = carRepository.findById(unitId)
                    .orElseThrow(() -> new RuntimeException("Unidad no encontrada"));

            // Validar que no se ponga AVAILABLE sin placa y color
            if ("AVAILABLE".equals(request.getStatus())) {
                String plate = request.getPlate() != null ? request.getPlate().trim() : unit.getPlate();
                String color = request.getColor() != null ? request.getColor().trim() : unit.getColor();
                if (plate == null || plate.isEmpty() || color == null || color.isEmpty()) {
                    return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "error", "Para marcar como disponible debe ingresar placa y color"
                    ));
                }
            }

            if (request.getPlate() != null) unit.setPlate(request.getPlate().trim());
            if (request.getColor() != null) unit.setColor(request.getColor().trim());
            if (request.getNotes() != null) unit.setNotes(request.getNotes());
            if (request.getBranchId() != null) {
                Branch branch = branchRepository.findById(request.getBranchId())
                        .orElseThrow(() -> new RuntimeException("Sede no encontrada"));
                unit.setBranch(branch);
            }
            if (request.getStatus() != null) {
                unit.setStatus(CarStatus.valueOf(request.getStatus()));
            }

            carRepository.save(unit);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Unidad actualizada exitosamente",
                "unit", new CarUnitResponse(
                    unit.getId(), unit.getCarModel().getId(),
                    unit.getCarModel().getBrand(), unit.getCarModel().getModel(),
                    unit.getCarModel().getYear(), unit.getPlate(), unit.getColor(),
                    unit.getStatus().name(), unit.getBranch().getName(),
                    unit.getBranch().getId(), unit.getNotes()
                )
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", "Estado inválido: " + request.getStatus()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /** Elimina una unidad individual */
    @DeleteMapping("/units/{unitId}")
    public ResponseEntity<?> deleteUnit(@PathVariable Long unitId) {
        try {
            Car unit = carRepository.findById(unitId)
                    .orElseThrow(() -> new RuntimeException("Unidad no encontrada"));
            carRepository.delete(unit);
            return ResponseEntity.ok(Map.of("success", true, "message", "Unidad eliminada exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    // Mantener compatibilidad con endpoints anteriores
    @GetMapping
    public ResponseEntity<?> getAllCars() {
        return getAllModels();
    }
}
