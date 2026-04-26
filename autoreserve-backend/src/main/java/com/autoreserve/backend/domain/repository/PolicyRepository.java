package com.autoreserve.backend.domain.repository;

import com.autoreserve.backend.domain.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PolicyRepository extends JpaRepository<Policy, Long> {
    Optional<Policy> findBySlug(String slug);
    List<Policy> findAllByOrderBySortOrderAsc();

    @Query("SELECT COALESCE(MAX(p.sortOrder), 0) FROM Policy p")
    Integer findMaxSortOrder();
}
