package com.autoreserve.backend.domain.service;

import com.autoreserve.backend.domain.entity.CarModel;
import com.autoreserve.backend.domain.entity.Favorite;
import com.autoreserve.backend.domain.entity.User;
import com.autoreserve.backend.domain.repository.FavoriteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;

    public FavoriteService(FavoriteRepository favoriteRepository) {
        this.favoriteRepository = favoriteRepository;
    }

    /** Guardar favorito */
    public Favorite save(Favorite favorite) {
        return favoriteRepository.save(favorite);
    }

    /** Eliminar favorito por ID */
    public void deleteById(Long id) {
        favoriteRepository.deleteById(id);
    }

    /** Agregar modelo a favoritos */
    public Favorite addToFavorites(User user, CarModel carModel) {
        // Verificar si ya existe
        Optional<Favorite> existing = favoriteRepository.findByUserAndCarModel(user, carModel);
        if (existing.isPresent()) {
            throw new RuntimeException("El modelo ya está en favoritos");
        }
        
        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setCarModel(carModel);
        favorite.setCreatedAt(LocalDateTime.now());
        
        return favoriteRepository.save(favorite);
    }

    /** Remover modelo de favoritos */
    public void removeFromFavorites(User user, CarModel carModel) {
        Favorite favorite = favoriteRepository.findByUserAndCarModel(user, carModel)
                .orElseThrow(() -> new RuntimeException("El modelo no está en favoritos"));
        favoriteRepository.delete(favorite);
    }

    /** Verificar si un modelo es favorito */
    public boolean isFavorite(User user, CarModel carModel) {
        return favoriteRepository.findByUserAndCarModel(user, carModel).isPresent();
    }

    /** Obtener favoritos de un usuario */
    public List<Favorite> getUserFavorites(User user) {
        return favoriteRepository.findByUserOrderByCreatedAtDesc(user);
    }

    /** Obtener IDs de modelos favoritos de un usuario */
    public Set<Long> getFavoriteModelIds(Long userId) {
        return favoriteRepository.findFavoriteCarModelIdsByUserId(userId);
    }

    /** Obtener estadísticas de favoritos por modelo */
    public List<Object[]> getFavoriteStatistics() {
        return favoriteRepository.countFavoritesByCarModel();
    }

    /** Obtener usuarios que tienen un modelo como favorito */
    public List<User> getUsersWithFavoriteModel(Long carModelId) {
        return favoriteRepository.findUsersByFavoriteCarModel(carModelId);
    }
}
