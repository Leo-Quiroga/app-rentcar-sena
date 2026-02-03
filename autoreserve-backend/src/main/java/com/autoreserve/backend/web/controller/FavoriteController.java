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
        
        User user = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Auto no encontrado"));
        
        // Verificar si ya existe en favoritos
        if (favoriteRepository.findByUserAndCar(user, car).isPresent()) {
            return ResponseEntity.badRequest()
                    .body("El auto ya está en favoritos");
        }
        
        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setCar(car);
        favorite.setCreatedAt(LocalDateTime.now());
        
        favoriteRepository.save(favorite);
        
        return ResponseEntity.ok("Auto agregado a favoritos");
    }

    @DeleteMapping("/{carId}")
    public ResponseEntity<?> removeFromFavorites(
            @PathVariable Long carId,
            @AuthenticationPrincipal UserDetails principal) {
        
        User user = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Auto no encontrado"));
        
        Favorite favorite = favoriteRepository.findByUserAndCar(user, car)
                .orElseThrow(() -> new RuntimeException("Favorito no encontrado"));
        
        favoriteRepository.delete(favorite);
        
        return ResponseEntity.ok("Auto removido de favoritos");
    }
}