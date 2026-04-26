package com.autoreserve.backend.domain.repository;

import com.autoreserve.backend.domain.entity.Faq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FaqRepository extends JpaRepository<Faq, Long> {
    List<Faq> findAllByOrderBySortOrderAsc();

    @Query("SELECT COALESCE(MAX(f.sortOrder), 0) FROM Faq f")
    Integer findMaxSortOrder();
}
