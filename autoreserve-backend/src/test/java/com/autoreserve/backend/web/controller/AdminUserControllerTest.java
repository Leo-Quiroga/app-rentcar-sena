package com.autoreserve.backend.web.controller;

import com.autoreserve.backend.domain.entity.Role;
import com.autoreserve.backend.domain.entity.User;
import com.autoreserve.backend.domain.entity.UserProfile;
import com.autoreserve.backend.domain.repository.RoleRepository;
import com.autoreserve.backend.domain.repository.UserProfileRepository;
import com.autoreserve.backend.domain.repository.UserRepository;
import com.autoreserve.backend.domain.service.UserProfileService;
import com.autoreserve.backend.dto.profile.UpdateProfileRequest;
import com.autoreserve.backend.dto.user.CreateUserRequest;
import com.autoreserve.backend.dto.user.UpdateUserRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(roles = "ADMIN")
class AdminUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private RoleRepository roleRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private UserProfileRepository userProfileRepository;

    @MockitoBean
    private UserProfileService userProfileService;

    private User testUser;
    private Role clientRole;
    private Role adminRole;

    @BeforeEach
    void setUp() {
        clientRole = new Role();
        clientRole.setId(1L);
        clientRole.setName("CLIENT");

        adminRole = new Role();
        adminRole.setId(2L);
        adminRole.setName("ADMIN");

        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setEmail("test@example.com");
        testUser.setPhone("987654321");
        testUser.setPasswordHash("hashedPassword");
        testUser.setRole(clientRole);
    }

    @Test
    void listUsers_ReturnsPagedUsers() throws Exception {
        when(userRepository.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(testUser)));

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getUserById_ExistingUser_ReturnsUser() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        mockMvc.perform(get("/api/admin/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("test@example.com"));
    }

    @Test
    void getUserById_NonExisting_ReturnsBadRequest() throws Exception {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/admin/users/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void createUser_ValidData_ReturnsSuccess() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setFirstName("New");
        request.setLastName("User");
        request.setEmail("new@example.com");
        request.setPhone("123456789");
        request.setPassword("password123");
        request.setRole("CLIENT");

        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(roleRepository.findByName("CLIENT")).thenReturn(Optional.of(clientRole));
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        mockMvc.perform(post("/api/admin/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void createUser_DuplicateEmail_ReturnsBadRequest() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setFirstName("Test");
        request.setLastName("User");
        request.setEmail("test@example.com");
        request.setPhone("123456789");
        request.setPassword("password123");
        request.setRole("CLIENT");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        mockMvc.perform(post("/api/admin/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void updateUser_ExistingUser_ReturnsSuccess() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setFirstName("Updated");
        request.setLastName("User");
        request.setEmail("updated@example.com");
        request.setPhone("111222333");
        request.setRole("CLIENT");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(roleRepository.findByName("CLIENT")).thenReturn(Optional.of(clientRole));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        mockMvc.perform(put("/api/admin/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void deleteUser_ClientUser_ReturnsSuccess() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        mockMvc.perform(delete("/api/admin/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void deleteUser_AdminUser_ReturnsBadRequest() throws Exception {
        User adminUser = new User();
        adminUser.setId(2L);
        adminUser.setRole(adminRole);

        when(userRepository.findById(2L)).thenReturn(Optional.of(adminUser));

        mockMvc.perform(delete("/api/admin/users/2"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void getMyProfile_AuthenticatedUser_ReturnsProfile() throws Exception {
        UserProfile profile = new UserProfile();
        profile.setUser(testUser);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(userProfileRepository.findByUser(testUser)).thenReturn(Optional.of(profile));

        mockMvc.perform(get("/api/admin/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void createUser_RoleNotFound_ReturnsBadRequest() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setFirstName("New");
        request.setLastName("User");
        request.setEmail("new@example.com");
        request.setPhone("123456789");
        request.setPassword("password123");
        request.setRole("CLIENT");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(roleRepository.findByName("CLIENT")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/admin/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void updateUser_WithPassword_ReturnsSuccess() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setFirstName("Updated");
        request.setLastName("User");
        request.setEmail("updated@example.com");
        request.setPhone("111222333");
        request.setRole("CLIENT");
        request.setPassword("newPass123");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(roleRepository.findByName("CLIENT")).thenReturn(Optional.of(clientRole));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        mockMvc.perform(put("/api/admin/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void updateUser_RoleNotFound_ReturnsBadRequest() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setFirstName("Updated");
        request.setLastName("User");
        request.setEmail("updated@example.com");
        request.setPhone("111222333");
        request.setRole("INVALID_ROLE");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(roleRepository.findByName("INVALID_ROLE")).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/admin/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void getMyProfile_CreatesProfileWhenMissing_ReturnsSuccess() throws Exception {
        UserProfile profile = new UserProfile();
        profile.setUser(testUser);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(userProfileRepository.findByUser(testUser)).thenReturn(Optional.empty());
        when(userProfileService.save(any(UserProfile.class))).thenReturn(profile);

        mockMvc.perform(get("/api/admin/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void updateMyProfile_ExistingProfile_ReturnsSuccess() throws Exception {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFirstName("Updated");
        request.setLastName("User");
        request.setEmail("updated@example.com");
        request.setPhone("111222333");
        request.setAddress("Test Address");
        request.setCity("Test City");
        request.setBirthDate(java.time.LocalDate.of(1990, 1, 1));
        request.setDrivingLicense("DL123456");

        UserProfile profile = new UserProfile();
        profile.setUser(testUser);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(userProfileRepository.findByUser(testUser)).thenReturn(Optional.of(profile));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userProfileService.save(any(UserProfile.class))).thenReturn(profile);

        mockMvc.perform(put("/api/admin/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void updateMyProfile_ProfileNotFound_ReturnsBadRequest() throws Exception {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFirstName("Updated");
        request.setLastName("User");
        request.setEmail("updated@example.com");
        request.setPhone("111222333");
        request.setAddress("Test Address");
        request.setCity("Test City");
        request.setBirthDate(java.time.LocalDate.of(1990, 1, 1));
        request.setDrivingLicense("DL123456");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(userProfileRepository.findByUser(testUser)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        mockMvc.perform(put("/api/admin/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
