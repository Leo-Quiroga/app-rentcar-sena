package com.autoreserve.backend.web.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.autoreserve.backend.domain.entity.Car;
import com.autoreserve.backend.domain.entity.Favorite;
import com.autoreserve.backend.domain.entity.User;
import com.autoreserve.backend.domain.repository.CarRepository;
import com.autoreserve.backend.domain.repository.FavoriteRepository;
import com.autoreserve.backend.domain.repository.UserRepository;
import com.autoreserve.backend.dto.car.CarResponse;

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
                favorite.getCar().getCarModel().getBrand(),
                favorite.getCar().getCarModel().getModel(),
                favorite.getCar().getCarModel().getYear(),
                favorite.getCar().getPlate(),
                favorite.getCar().getCarModel().getPricePerDay(),
                favorite.getCar().getStatus().name(),
                favorite.getCar().getCarModel().getCategory().getName(),
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