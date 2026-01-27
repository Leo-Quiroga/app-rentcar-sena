package com.autoreserve.backend.domain.repository;

import com.autoreserve.backend.domain.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
