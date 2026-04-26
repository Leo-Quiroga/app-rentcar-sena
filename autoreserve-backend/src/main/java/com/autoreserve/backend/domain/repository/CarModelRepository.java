package com.autoreserve.backend.domain.repository;

import com.autoreserve.backend.domain.entity.CarModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface CarModelRepository extends JpaRepository<CarModel, Long> {

    // Modelos por categoría
    List<CarModel> findByCategoryId(Long categoryId);

    /**
     * Catálogo público: modelos que tienen al menos 1 unidad AVAILABLE
     * y que no están completamente reservadas en el rango de fechas.
     * Retorna el modelo con la cantidad de unidades disponibles.
     */
    @Query("SELECT DISTINCT cm FROM CarModel cm " +
           "JOIN cm.units u " +
           "WHERE u.status = 'AVAILABLE' " +
           "AND (:categoryId IS NULL OR cm.category.id = :categoryId) " +
           "AND u.id NOT IN (" +
           "  SELECT r.car.id FROM Reservation r " +
           "  WHERE (r.status = 'CONFIRMED' OR r.status = 'IN_PROGRESS') " +
           "  AND (:startDate <= r.endDate) AND (:endDate >= r.startDate)" +
           ")")
    List<CarModel> findAvailableModels(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("categoryId") Long categoryId);

    /**
     * Todos los modelos que tienen al menos 1 unidad AVAILABLE (sin filtro de fechas)
     */
    @Query("SELECT DISTINCT cm FROM CarModel cm " +
           "JOIN cm.units u " +
           "WHERE u.status = 'AVAILABLE' " +
           "AND (:categoryId IS NULL OR cm.category.id = :categoryId)")
    List<CarModel> findModelsWithAvailableUnits(@Param("categoryId") Long categoryId);
}
