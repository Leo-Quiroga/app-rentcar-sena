package com.autoreserve.backend.domain.service;

import com.autoreserve.backend.domain.entity.UserProfile;
import com.autoreserve.backend.domain.repository.UserProfileRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    public UserProfileService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    public UserProfile save(UserProfile profile) {
        return userProfileRepository.save(profile);
    }

    public Optional<UserProfile> findById(Long id) {
        return userProfileRepository.findById(id);
    }
}
