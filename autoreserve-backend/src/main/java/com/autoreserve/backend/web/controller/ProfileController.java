package com.autoreserve.backend.web.controller;

import com.autoreserve.backend.domain.entity.User;
import com.autoreserve.backend.domain.entity.UserProfile;
import com.autoreserve.backend.domain.repository.UserProfileRepository;
import com.autoreserve.backend.domain.repository.UserRepository;
import com.autoreserve.backend.domain.service.UserProfileService;
import com.autoreserve.backend.dto.profile.ProfileResponse;
import com.autoreserve.backend.dto.profile.UpdateProfileRequest;

import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserProfileService userProfileService;

    public ProfileController(
            UserRepository userRepository,
            UserProfileRepository userProfileRepository,
            UserProfileService userProfileService
    ) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.userProfileService = userProfileService;
    }

    @GetMapping("/me")
    public ProfileResponse getMyProfile(
            @AuthenticationPrincipal UserDetails principal
    ) {
        // 1️ Recuperar la entidad User real
        User user = userRepository
                .findByEmail(principal.getUsername())
                .orElseThrow();

        // 2️ Buscar o crear UserProfile
        UserProfile profile = userProfileRepository
                .findByUser(user)
                .orElseGet(() -> {
                    UserProfile newProfile = new UserProfile();
                    newProfile.setUser(user);
                    return userProfileService.save(newProfile);
                });

        // 3️ Responder con datos combinados
        return new ProfileResponse(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                profile.getAddress(),
                profile.getCity(),
                profile.getBirthDate(),
                profile.getDrivingLicense(),
                user.getCreatedAt()
        );
    }

    @PutMapping("/me")
    public void updateMyProfile(
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        // 1️ User
        User user = userRepository
                .findByEmail(principal.getUsername())
                .orElseThrow();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());

        userRepository.save(user);

        // 2️ UserProfile
        UserProfile profile = userProfileRepository
                .findByUser(user)
                .orElseThrow();

        profile.setAddress(request.getAddress());
        profile.setCity(request.getCity());
        profile.setBirthDate(request.getBirthDate());
        profile.setDrivingLicense(request.getDrivingLicense());

        userProfileService.save(profile);
    }
}
