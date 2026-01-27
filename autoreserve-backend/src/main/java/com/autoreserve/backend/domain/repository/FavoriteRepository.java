package com.autoreserve.backend.domain.repository;

import com.autoreserve.backend.domain.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
}
