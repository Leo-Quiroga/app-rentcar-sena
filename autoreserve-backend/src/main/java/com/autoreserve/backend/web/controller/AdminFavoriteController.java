package com.autoreserve.backend.web.controller;

import com.autoreserve.backend.domain.entity.CarModel;
import com.autoreserve.backend.domain.entity.User;
import com.autoreserve.backend.domain.repository.CarModelRepository;
import com.autoreserve.backend.domain.repository.CarRepository;
import com.autoreserve.backend.domain.repository.FavoriteRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/favorites")
@PreAuthorize("hasRole('ADMIN')")
public class AdminFavoriteController {

    private final FavoriteRepository favoriteRepository;
    private final CarModelRepository carModelRepository;
    private final CarRepository carRepository;

    public AdminFavoriteController(FavoriteRepository favoriteRepository,
                                   CarModelRepository carModelRepository,
                                   CarRepository carRepository) {
        this.favoriteRepository = favoriteRepository;
        this.carModelRepository = carModelRepository;
        this.carRepository = carRepository;
    }

    /** Estadísticas generales de favoritos */
    @GetMapping("/stats")
    public ResponseEntity<?> getFavoriteStats() {
        try {
            // Obtener estadísticas por modelo
            List<Object[]> favoritesByModel = favoriteRepository.countFavoritesByCarModel();
            
            List<Map<String, Object>> modelStats = favoritesByModel.stream()
                    .map(row -> {
                        Long carModelId = (Long) row[0];
                        Long favoriteCount = (Long) row[1];
                        
                        CarModel model = carModelRepository.findById(carModelId).orElse(null);
                        if (model == null) return null;
                        
                        long availableUnits = carRepository.countAvailableByModel(carModelId);
                        long totalUnits = carRepository.countByCarModelId(carModelId);
                        
                        Map<String, Object> stat = new HashMap<>();
                        stat.put("carModelId", carModelId);
                        stat.put("brand", model.getBrand());
                        stat.put("model", model.getModel());
                        stat.put("year", model.getYear());
                        stat.put("categoryName", model.getCategory().getName());
                        stat.put("favoriteCount", favoriteCount);
                        stat.put("availableUnits", availableUnits);
                        stat.put("totalUnits", totalUnits);
                        stat.put("pricePerDay", model.getPricePerDay());
                        
                        return stat;
                    })
                    .filter(stat -> stat != null)
                    .sorted((a, b) -> Long.compare((Long) b.get("favoriteCount"), (Long) a.get("favoriteCount")))
                    .collect(Collectors.toList());

            // Estadísticas generales
            long totalFavorites = favoriteRepository.count();
            long totalModels = carModelRepository.count();
            long modelsWithFavorites = favoritesByModel.size();
            
            Map<String, Object> generalStats = new HashMap<>();
            generalStats.put("totalFavorites", totalFavorites);
            generalStats.put("totalModels", totalModels);
            generalStats.put("modelsWithFavorites", modelsWithFavorites);
            generalStats.put("averageFavoritesPerModel", 
                modelsWithFavorites > 0 ? (double) totalFavorites / modelsWithFavorites : 0.0);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Estadísticas de favoritos obtenidas exitosamente",
                    "generalStats", generalStats,
                    "modelStats", modelStats,
                    "topModels", modelStats.stream().limit(10).collect(Collectors.toList())
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", "Error interno del servidor", "details", e.getMessage()));
        }
    }

    /** Obtener usuarios que tienen un modelo como favorito */
    @GetMapping("/model/{carModelId}/users")
    public ResponseEntity<?> getUsersWithFavoriteModel(@PathVariable Long carModelId) {
        try {
            CarModel carModel = carModelRepository.findById(carModelId)
                    .orElseThrow(() -> new RuntimeException("Modelo no encontrado con ID: " + carModelId));

            List<User> users = favoriteRepository.findUsersByFavoriteCarModel(carModelId);
            
            List<Map<String, Object>> userList = users.stream()
                    .map(user -> {
                        Map<String, Object> userInfo = new HashMap<>();
                        userInfo.put("userId", user.getId());
                        userInfo.put("firstName", user.getFirstName());
                        userInfo.put("lastName", user.getLastName());
                        userInfo.put("email", user.getEmail());
                        return userInfo;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Usuarios con modelo favorito obtenidos exitosamente",
                    "carModel", Map.of(
                            "id", carModel.getId(),
                            "brand", carModel.getBrand(),
                            "model", carModel.getModel(),
                            "year", carModel.getYear()
                    ),
                    "users", userList,
                    "count", userList.size()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", "Error interno del servidor", "details", e.getMessage()));
        }
    }

    /** Top modelos más populares */
    @GetMapping("/top-models")
    public ResponseEntity<?> getTopFavoriteModels() {
        try {
            List<Object[]> favoritesByModel = favoriteRepository.countFavoritesByCarModel();
            
            List<Map<String, Object>> topModels = favoritesByModel.stream()
                    .map(row -> {
                        Long carModelId = (Long) row[0];
                        Long favoriteCount = (Long) row[1];
                        
                        CarModel model = carModelRepository.findById(carModelId).orElse(null);
                        if (model == null) return null;
                        
                        Map<String, Object> modelInfo = new HashMap<>();
                        modelInfo.put("carModelId", carModelId);
                        modelInfo.put("brand", model.getBrand());
                        modelInfo.put("model", model.getModel());
                        modelInfo.put("year", model.getYear());
                        modelInfo.put("categoryName", model.getCategory().getName());
                        modelInfo.put("favoriteCount", favoriteCount);
                        modelInfo.put("pricePerDay", model.getPricePerDay());
                        modelInfo.put("image", model.getImage());
                        
                        return modelInfo;
                    })
                    .filter(model -> model != null)
                    .sorted((a, b) -> Long.compare((Long) b.get("favoriteCount"), (Long) a.get("favoriteCount")))
                    .limit(20)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Top modelos favoritos obtenidos exitosamente",
                    "topModels", topModels,
                    "count", topModels.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", "Error interno del servidor", "details", e.getMessage()));
        }
    }
}