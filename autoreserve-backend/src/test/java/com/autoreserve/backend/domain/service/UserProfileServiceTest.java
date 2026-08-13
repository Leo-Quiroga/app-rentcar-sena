package com.autoreserve.backend.domain.service;

import com.autoreserve.backend.domain.entity.UserProfile;
import com.autoreserve.backend.domain.repository.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @InjectMocks
    private UserProfileService userProfileService;

    private UserProfile testProfile;

    @BeforeEach
    void setUp() {
        testProfile = new UserProfile();
        testProfile.setAddress("Av. Principal 123");
        testProfile.setCity("Lima");
        testProfile.setBirthDate(LocalDate.of(1990, 1, 1));
        testProfile.setDrivingLicense("12345678");
    }

    @Test
    void save_ReturnsSavedProfile() {
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(testProfile);

        UserProfile result = userProfileService.save(testProfile);

        assertNotNull(result);
        assertEquals("Av. Principal 123", result.getAddress());
        verify(userProfileRepository, times(1)).save(testProfile);
    }

    @Test
    void findById_ExistingProfile_ReturnsOptional() {
        UserProfile profile = new UserProfile();
        profile.setAddress("Av. Principal 123");
        when(userProfileRepository.findById(1L)).thenReturn(Optional.of(profile));

        Optional<UserProfile> result = userProfileService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Av. Principal 123", result.get().getAddress());
        verify(userProfileRepository, times(1)).findById(1L);
    }

    @Test
    void findById_NonExistingProfile_ReturnsEmpty() {
        when(userProfileRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<UserProfile> result = userProfileService.findById(999L);

        assertFalse(result.isPresent());
        verify(userProfileRepository, times(1)).findById(999L);
    }
}
