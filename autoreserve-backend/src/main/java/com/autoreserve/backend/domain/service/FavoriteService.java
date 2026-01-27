package com.autoreserve.backend.domain.service;

import com.autoreserve.backend.domain.entity.Favorite;
import com.autoreserve.backend.domain.repository.FavoriteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;

    public FavoriteService(FavoriteRepository favoriteRepository) {
        this.favoriteRepository = favoriteRepository;
    }

    public Favorite save(Favorite favorite) {
        return favoriteRepository.save(favorite);
    }

    public void deleteById(Long id) {
        favoriteRepository.deleteById(id);
    }
}
