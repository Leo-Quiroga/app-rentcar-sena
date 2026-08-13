package com.autoreserve.backend.domain.service;

import com.autoreserve.backend.domain.entity.Role;
import com.autoreserve.backend.domain.entity.User;
import com.autoreserve.backend.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setName("CLIENT");

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setRole(role);
    }

    @Test
    void save_ReturnsPersistedUser() {
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.save(testUser);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        verify(userRepository).save(testUser);
    }

    @Test
    void findAll_ReturnsAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(testUser));

        List<User> result = userService.findAll();

        assertThat(result).hasSize(1);
    }

    @Test
    void findById_ExistingId_ReturnsUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void findById_NonExistingId_ReturnsEmpty() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<User> result = userService.findById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void findByEmail_ExistingEmail_ReturnsUser() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.findByEmail("test@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getFirstName()).isEqualTo("Test");
    }

    @Test
    void findByEmail_NonExistingEmail_ReturnsEmpty() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        Optional<User> result = userService.findByEmail("notfound@example.com");

        assertThat(result).isEmpty();
    }

    @Test
    void deleteById_CallsRepository() {
        userService.deleteById(1L);

        verify(userRepository).deleteById(1L);
    }
}
