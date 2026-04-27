package com.autoreserve.backend.web.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.autoreserve.backend.domain.entity.CarModel;
import com.autoreserve.backend.domain.entity.Favorite;
import com.autoreserve.backend.domain.entity.User;
import com.autoreserve.backend.domain.repository.CarModelRepository;
import com.autoreserve.backend.domain.repository.CarRepository;
import com.autoreserve.backend.domain.repository.FavoriteRepository;
import com.autoreserve.backend.domain.repository.UserRepository;
import com.autoreserve.backend.dto.favorite.FavoriteResponse;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteRepository favoriteRepository;
    private final CarRepository carRepository;
    private final CarModelRepository carModelRepository;
    private final UserRepository userRepository;

    public FavoriteController(FavoriteRepository favoriteRepository, 
                            CarRepository carRepository,
                            CarModelRepository carModelRepository,
                            UserRepository userRepository) {
        this.favoriteRepository = favoriteRepository;
        this.carRepository = carRepository;
        this.carModelRepository = carModelRepository;
        this.userRepository = userRepository;
    }

    /** Obtener mis modelos favoritos con información completa */
    @GetMapping("/my")
    public ResponseEntity<?> getMyFavorites(@AuthenticationPrincipal UserDetails principal) {
        try {
            User user = userRepository.findByEmail(principal.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            List<Favorite> favorites = favoriteRepository.findByUserOrderByCreatedAtDesc(user);
            
            List<FavoriteResponse> responses = favorites.stream()
                    .map(favorite -> {
                        CarModel model = favorite.getCarModel();
                        long available = carRepository.countAvailableByModel(model.getId());
                        long total = carRepository.countByCarModelId(model.getId());
                        
                        return new FavoriteResponse(
                                favorite.getId(),
                                model.getId(),
                                model.getBrand(),
                                model.getModel(),
                                model.getYear(),
                                model.getPricePerDay(),
                                model.getImage(),
                                model.getDescription(),
                                model.getCategory().getName(),
                                available,
                                total,
                                favorite.getCreatedAt()
                        );
                    })
                    .toList();
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Favoritos obtenidos exitosamente",
                    "data", responses,
                    "count", responses.size()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", "Error interno del servidor", "details", e.getMessage()));
        }
    }

    /** Agregar modelo a favoritos */
    @PostMapping
    @Transactional
    public ResponseEntity<?> addToFavorites(
            @RequestBody Map<String, Long> request,
            @AuthenticationPrincipal UserDetails principal) {
        
        try {
            System.out.println("=== DEBUG addToFavorites ===");
            System.out.println("Request body: " + request);
            System.out.println("Principal: " + (principal != null ? principal.getUsername() : "null"));
            
            Long carModelId = request.get("carModelId");
            System.out.println("Extracted carModelId: " + carModelId);
            
            if (carModelId == null) {
                System.out.println("ERROR: carModelId is null");
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "carModelId es requerido"));
            }
            User user = userRepository.findByEmail(principal.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            System.out.println("Found user: " + user.getId() + " - " + user.getEmail());
            
            CarModel carModel = carModelRepository.findById(carModelId)
                    .orElseThrow(() -> new RuntimeException("Modelo no encontrado con ID: " + carModelId));
            System.out.println("Found carModel: " + carModel.getId() + " - " + carModel.getBrand() + " " + carModel.getModel());
            
            // Verificar si ya existe usando IDs directamente (más confiable)
            boolean alreadyExists = favoriteRepository.existsByUserIdAndCarModelId(user.getId(), carModelId);
            System.out.println("Already exists check: " + alreadyExists);
            
            if (alreadyExists) {
                System.out.println("ERROR: Already exists in favorites");
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "El modelo ya está en tu lista de favoritos"));
            }
            
            Favorite favorite = new Favorite();
            favorite.setUser(user);
            favorite.setCarModel(carModel);
            favorite.setCreatedAt(LocalDateTime.now());
            
            System.out.println("About to save favorite...");
            Favorite saved = favoriteRepository.save(favorite);
            System.out.println("Favorite saved with ID: " + saved.getId());
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Modelo agregado a favoritos exitosamente",
                    "carModelId", carModelId,
                    "favoriteId", saved.getId(),
                    "modelName", carModel.getBrand() + " " + carModel.getModel() + " " + carModel.getYear()
            ));
        } catch (RuntimeException e) {
            System.out.println("RuntimeException: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        } catch (Exception e) {
            System.out.println("General Exception: " + e.getMessage());
            e.printStackTrace();
            // Manejar errores de constraint de BD
            String errorMessage = e.getMessage();
            if (errorMessage != null && (errorMessage.contains("Duplicate entry") || 
                                       errorMessage.contains("constraint") ||
                                       errorMessage.contains("unique"))) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "El modelo ya está en tu lista de favoritos"));
            }
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", "Error interno del servidor", "details", errorMessage));
        }
    }

    /** Remover modelo de favoritos */
    @DeleteMapping("/{carModelId}")
    @Transactional
    public ResponseEntity<?> removeFromFavorites(
            @PathVariable Long carModelId,
            @AuthenticationPrincipal UserDetails principal) {
        
        try {
            User user = userRepository.findByEmail(principal.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            CarModel carModel = carModelRepository.findById(carModelId)
                    .orElseThrow(() -> new RuntimeException("Modelo no encontrado con ID: " + carModelId));
            
            Optional<Favorite> favoriteOpt = favoriteRepository.findByUserAndCarModel(user, carModel);
            if (favoriteOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "El modelo no está en tu lista de favoritos"));
            }
            
            favoriteRepository.delete(favoriteOpt.get());
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Modelo removido de favoritos exitosamente",
                    "carModelId", carModelId,
                    "modelName", carModel.getBrand() + " " + carModel.getModel() + " " + carModel.getYear()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", "Error interno del servidor", "details", e.getMessage()));
        }
    }

    /** Verificar si un modelo es favorito (para UI) */
    @GetMapping("/check/{carModelId}")
    public ResponseEntity<?> isFavorite(
            @PathVariable Long carModelId,
            @AuthenticationPrincipal UserDetails principal) {
        
        try {
            User user = userRepository.findByEmail(principal.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            boolean isFavorite = favoriteRepository.existsByUserIdAndCarModelId(user.getId(), carModelId);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "isFavorite", isFavorite,
                    "carModelId", carModelId
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /** Obtener IDs de modelos favoritos (para marcar corazones en UI) */
    @GetMapping("/ids")
    public ResponseEntity<?> getFavoriteIds(@AuthenticationPrincipal UserDetails principal) {
        try {
            User user = userRepository.findByEmail(principal.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            Set<Long> favoriteIds = favoriteRepository.findFavoriteCarModelIdsByUserId(user.getId());
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "favoriteIds", favoriteIds,
                    "count", favoriteIds.size()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }
}