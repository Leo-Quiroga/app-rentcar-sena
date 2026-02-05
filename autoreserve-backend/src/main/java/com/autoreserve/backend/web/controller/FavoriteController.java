package com.autoreserve.backend.web.controller;

import com.autoreserve.backend.domain.entity.*;
import com.autoreserve.backend.domain.repository.*;
import com.autoreserve.backend.dto.car.CarResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteRepository favoriteRepository;
    private final CarRepository carRepository;
    private final UserRepository userRepository;

    public FavoriteController(FavoriteRepository favoriteRepository, 
                            CarRepository carRepository, 
                            UserRepository userRepository) {
        this.favoriteRepository = favoriteRepository;
        this.carRepository = carRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/my")
    public ResponseEntity<List<CarResponse>> getMyFavorites(
            @AuthenticationPrincipal UserDetails principal) {
        
        User user = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        List<Favorite> favorites = favoriteRepository.findByUserOrderByCreatedAtDesc(user);
        
        List<CarResponse> cars = favorites.stream()
                .map(favorite -> new CarResponse(
                        favorite.getCar().getId(),
                        favorite.getCar().getBrand(),
                        favorite.getCar().getModel(),
                        favorite.getCar().getYear(),
                        favorite.getCar().getPlate(),
                        favorite.getCar().getPricePerDay(),
                        favorite.getCar().getStatus().name(),
                        favorite.getCar().getCategory().getName(),
                        favorite.getCar().getBranch().getName(),
                        null
                ))
                .toList();
        
        return ResponseEntity.ok(cars);
    }

    @PostMapping
    public ResponseEntity<?> addToFavorites(
            @RequestParam Long carId,
            @AuthenticationPrincipal UserDetails principal) {
        
        try {
            User user = userRepository.findByEmail(principal.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            Car car = carRepository.findById(carId)
                    .orElseThrow(() -> new RuntimeException("Auto no encontrado con ID: " + carId));
            
            // Verificar si ya existe en favoritos
            if (favoriteRepository.findByUserAndCar(user, car).isPresent()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "El auto ya está en tu lista de favoritos"));
            }
            
            Favorite favorite = new Favorite();
            favorite.setUser(user);
            favorite.setCar(car);
            favorite.setCreatedAt(LocalDateTime.now());
            
            favoriteRepository.save(favorite);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Auto agregado a favoritos exitosamente",
                    "carId", carId
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", "Error interno del servidor", "details", e.getMessage()));
        }
    }

    @DeleteMapping("/{carId}")
    public ResponseEntity<?> removeFromFavorites(
            @PathVariable Long carId,
            @AuthenticationPrincipal UserDetails principal) {
        
        try {
            User user = userRepository.findByEmail(principal.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            Car car = carRepository.findById(carId)
                    .orElseThrow(() -> new RuntimeException("Auto no encontrado con ID: " + carId));
            
            Favorite favorite = favoriteRepository.findByUserAndCar(user, car)
                    .orElseThrow(() -> new RuntimeException("El auto no está en tu lista de favoritos"));
            
            favoriteRepository.delete(favorite);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Auto removido de favoritos exitosamente",
                    "carId", carId
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