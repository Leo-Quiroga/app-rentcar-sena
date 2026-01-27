package com.autoreserve.backend.domain.repository;

import com.autoreserve.backend.domain.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
