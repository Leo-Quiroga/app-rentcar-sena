package com.autoreserve.backend.domain.repository;

import com.autoreserve.backend.domain.entity.CarModel;
import com.autoreserve.backend.domain.entity.Favorite;
import com.autoreserve.backend.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    
    // Obtener favoritos de un usuario ordenados por fecha de creación
    List<Favorite> findByUserOrderByCreatedAtDesc(User user);
    
    // Verificar si un modelo es favorito de un usuario
    Optional<Favorite> findByUserAndCarModel(User user, CarModel carModel);
    
    // Verificar si un modelo es favorito (por IDs)
    @Query("SELECT COUNT(f) > 0 FROM Favorite f WHERE f.user.id = :userId AND f.carModel.id = :carModelId")
    boolean existsByUserIdAndCarModelId(@Param("userId") Long userId, @Param("carModelId") Long carModelId);
    
    // Obtener IDs de modelos favoritos de un usuario (para marcar en UI)
    @Query("SELECT f.carModel.id FROM Favorite f WHERE f.user.id = :userId")
    Set<Long> findFavoriteCarModelIdsByUserId(@Param("userId") Long userId);
    
    // Contar favoritos por modelo (para estadísticas admin)
    @Query("SELECT f.carModel.id, COUNT(f) FROM Favorite f GROUP BY f.carModel.id")
    List<Object[]> countFavoritesByCarModel();
    
    // Obtener usuarios que tienen un modelo como favorito (para notificaciones)
    @Query("SELECT f.user FROM Favorite f WHERE f.carModel.id = :carModelId")
    List<User> findUsersByFavoriteCarModel(@Param("carModelId") Long carModelId);
}
