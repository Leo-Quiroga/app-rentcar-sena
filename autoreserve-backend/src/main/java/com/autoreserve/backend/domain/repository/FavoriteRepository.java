package com.autoreserve.backend.domain.repository;

import com.autoreserve.backend.domain.entity.Car;
import com.autoreserve.backend.domain.entity.Favorite;
import com.autoreserve.backend.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserOrderByCreatedAtDesc(User user);
    Optional<Favorite> findByUserAndCar(User user, Car car);
}
