package com.autoreserve.backend.domain.repository;

import com.autoreserve.backend.domain.entity.User;
import com.autoreserve.backend.domain.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    Optional<UserProfile> findByUser(User user);
}
