package com.autoreserve.backend.domain.repository;

import com.autoreserve.backend.domain.entity.Car;
import com.autoreserve.backend.domain.entity.CarStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Long> {
    Page<Car> findByStatus(CarStatus status, Pageable pageable);
    Page<Car> findByCategoryIdAndStatus(Long categoryId, CarStatus status, Pageable pageable);
    Page<Car> findByBranchIdAndStatus(Long branchId, CarStatus status, Pageable pageable);
    Page<Car> findByCategoryIdAndBranchIdAndStatus(Long categoryId, Long branchId, CarStatus status, Pageable pageable);
}
