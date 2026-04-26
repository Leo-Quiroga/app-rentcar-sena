package com.autoreserve.backend.domain.repository;

import com.autoreserve.backend.domain.entity.Car;
import com.autoreserve.backend.domain.entity.CarModel;
import com.autoreserve.backend.domain.entity.CarStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CarRepository extends JpaRepository<Car, Long> {

    // Unidades por modelo
    List<Car> findByCarModel(CarModel carModel);
    List<Car> findByCarModelId(Long carModelId);

    // Unidades por estado
    Page<Car> findByStatus(CarStatus status, Pageable pageable);
    List<Car> findByStatus(CarStatus status);

    // Unidades por sede
    List<Car> findByBranchId(Long branchId);

    // Contar unidades disponibles por modelo
    @Query("SELECT COUNT(c) FROM Car c WHERE c.carModel.id = :modelId AND c.status = 'AVAILABLE'")
    long countAvailableByModel(@Param("modelId") Long modelId);

    // Contar unidades totales por modelo
    long countByCarModelId(Long carModelId);

    // Buscar primera unidad disponible de un modelo para asignar a reserva
    @Query("SELECT c FROM Car c WHERE c.carModel.id = :modelId " +
           "AND c.status = 'AVAILABLE' " +
           "AND c.id NOT IN (" +
           "  SELECT r.car.id FROM Reservation r " +
           "  WHERE (r.status = 'CONFIRMED' OR r.status = 'IN_PROGRESS') " +
           "  AND (:startDate <= r.endDate) AND (:endDate >= r.startDate)" +
           ") ORDER BY c.id ASC")
    List<Car> findAvailableUnitForModel(
            @Param("modelId") Long modelId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Verificar solapamiento de reservas para una unidad específica
    @Query("SELECT COUNT(r) > 0 FROM Reservation r WHERE r.car.id = :carId " +
           "AND (r.status = 'CONFIRMED' OR r.status = 'IN_PROGRESS') " +
           "AND (:startDate <= r.endDate) AND (:endDate >= r.startDate)")
    boolean existsOverlappingReservation(
            @Param("carId") Long carId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Buscar por placa
    Optional<Car> findByPlate(String plate);
}
