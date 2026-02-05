package com.autoreserve.backend.domain.repository;

import com.autoreserve.backend.domain.entity.Car;
import com.autoreserve.backend.domain.entity.CarStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long> {
    Page<Car> findByStatus(CarStatus status, Pageable pageable);
    Page<Car> findByCategoryIdAndStatus(Long categoryId, CarStatus status, Pageable pageable);
    Page<Car> findByBranchIdAndStatus(Long branchId, CarStatus status, Pageable pageable);
    Page<Car> findByCategoryIdAndBranchIdAndStatus(Long categoryId, Long branchId, CarStatus status, Pageable pageable);
    
    // Buscar autos disponibles excluyendo los que tienen reservas en el rango de fechas
    @Query("SELECT c FROM Car c WHERE c.status = 'AVAILABLE' " +
           "AND (:categoryId IS NULL OR c.category.id = :categoryId) " +
           "AND c.id NOT IN (" +
           "  SELECT r.car.id FROM Reservation r " +
           "  WHERE r.status = 'CONFIRMED' " +
           "  AND ((:startDate < r.endDate) AND (:endDate > r.startDate))" +
           ")")
    List<Car> findAvailableCars(@Param("startDate") LocalDate startDate, 
                               @Param("endDate") LocalDate endDate,
                               @Param("categoryId") Long categoryId);
}
