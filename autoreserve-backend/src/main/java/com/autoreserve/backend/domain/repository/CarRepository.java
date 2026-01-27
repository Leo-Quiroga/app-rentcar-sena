package com.autoreserve.backend.domain.repository;

import com.autoreserve.backend.domain.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Long> {
}
