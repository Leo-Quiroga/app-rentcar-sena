package com.autoreserve.backend.domain.repository;

import com.autoreserve.backend.domain.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BranchRepository extends JpaRepository<Branch, Long> {
}
