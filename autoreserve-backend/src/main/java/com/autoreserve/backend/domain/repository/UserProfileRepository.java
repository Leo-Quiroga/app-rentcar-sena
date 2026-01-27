package com.autoreserve.backend.domain.repository;

import com.autoreserve.backend.domain.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
}
