package com.autoreserve.backend.web.controller;

import com.autoreserve.backend.domain.entity.Role;
import com.autoreserve.backend.domain.entity.User;
import com.autoreserve.backend.domain.entity.UserProfile;
import com.autoreserve.backend.domain.repository.RoleRepository;
import com.autoreserve.backend.domain.repository.UserRepository;
import com.autoreserve.backend.domain.repository.UserProfileRepository;
import com.autoreserve.backend.domain.service.UserProfileService;
import com.autoreserve.backend.dto.user.CreateUserRequest;
import com.autoreserve.backend.dto.user.PagedUserResponse;
import com.autoreserve.backend.dto.user.UpdateUserRequest;
import com.autoreserve.backend.dto.user.UserResponse;
import com.autoreserve.backend.dto.profile.ProfileResponse;
import com.autoreserve.backend.dto.profile.UpdateProfileRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador de gestión administrativa de usuarios.
 * Proporciona operaciones CRUD protegidas exclusivamente para el rol ADMIN.
 * También incluye endpoints para que cualquier usuario gestione su propio perfil.
 */
@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserProfileRepository userProfileRepository;
    private final UserProfileService userProfileService;

    public AdminUserController(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            UserProfileRepository userProfileRepository,
            UserProfileService userProfileService
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userProfileRepository = userProfileRepository;
        this.userProfileService = userProfileService;
    }

    /**
     * Crea un nuevo usuario.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createClient(
            @Valid @RequestBody CreateUserRequest request
    ) {
        try {
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "El correo ya está registrado"));
            }
            
            Role clientRole = roleRepository.findByName(request.getRole().toUpperCase())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + request.getRole()));

            User user = new User();
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setEmail(request.getEmail());
            user.setPhone(request.getPhone());
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            user.setRole(clientRole);

            User saved = userRepository.save(user);

            UserResponse userData = new UserResponse(
                    saved.getId(),
                    saved.getFirstName(),
                    saved.getLastName(),
                    saved.getEmail(),
                    saved.getPhone(),
                    saved.getRole().getName(),
                    saved.getCreatedAt()
            );

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Usuario creado exitosamente",
                    "data", userData
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", "Error interno del servidor", "details", e.getMessage()));
        }
    }
    // Actualiza los datos de un usuario existente.
    // Permite modificar información de perfil y, de forma opcional, la credencial de acceso.
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setEmail(request.getEmail());
            user.setPhone(request.getPhone());

            if (request.getRole() != null) {
                Role role = roleRepository.findByName(request.getRole().toUpperCase())
                        .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + request.getRole()));
                user.setRole(role);
            }

            if (request.getPassword() != null && !request.getPassword().isBlank()) {
                user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            }

            User updated = userRepository.save(user);

            UserResponse userData = new UserResponse(
                    updated.getId(),
                    updated.getFirstName(),
                    updated.getLastName(),
                    updated.getEmail(),
                    updated.getPhone(),
                    updated.getRole().getName(),
                    updated.getCreatedAt()
            );

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Usuario actualizado exitosamente",
                    "data", userData
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", "Error interno del servidor", "details", e.getMessage()));
        }
    }

    /**
     * Elimina un usuario del sistema. Impide la eliminación de cuentas con rol ADMIN.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

            // Protección de seguridad básica
            if ("ADMIN".equals(user.getRole().getName())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "No se puede eliminar un usuario con rol ADMINISTRADOR"));
            }

            userRepository.delete(user);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Usuario eliminado exitosamente",
                    "deletedId", id
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", "Error interno del servidor", "details", e.getMessage()));
        }
    }
    // Recupera los detalles de un usuario específico por su ID.
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

            UserResponse userData = new UserResponse(
                    user.getId(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.getPhone(),
                    user.getRole().getName(),
                    user.getCreatedAt()
            );

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Usuario obtenido exitosamente",
                    "data", userData
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", "Error interno del servidor", "details", e.getMessage()));
        }
    }
    // Lista todos los usuarios con paginación.
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            Page<User> userPage = userRepository.findAll(PageRequest.of(page, size));

            List<UserResponse> users = userPage.getContent()
                    .stream()
                    .map(user -> new UserResponse(
                            user.getId(),
                            user.getFirstName(),
                            user.getLastName(),
                            user.getEmail(),
                            user.getPhone(),
                            user.getRole().getName(),
                            user.getCreatedAt()
                    ))
                    .toList();

            PagedUserResponse pagedData = new PagedUserResponse(
                    users,
                    userPage.getNumber(),
                    userPage.getSize(),
                    userPage.getTotalElements(),
                    userPage.getTotalPages()
            );

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Usuarios obtenidos exitosamente",
                    "data", pagedData
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", "Error interno del servidor", "details", e.getMessage()));
        }
    }

    // ================= ENDPOINTS DE PERFIL PROPIO =================

    /**
     * Obtiene el perfil completo del usuario autenticado (admin o cliente)
     */
    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(
            @AuthenticationPrincipal UserDetails principal
    ) {
        try {
            User user = userRepository
                    .findByEmail(principal.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            UserProfile profile = userProfileRepository
                    .findByUser(user)
                    .orElseGet(() -> {
                        UserProfile newProfile = new UserProfile();
                        newProfile.setUser(user);
                        return userProfileService.save(newProfile);
                    });

            ProfileResponse profileData = new ProfileResponse(
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

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Perfil obtenido exitosamente",
                    "data", profileData
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", "Error interno del servidor", "details", e.getMessage()));
        }
    }

    /**
     * Actualiza el perfil completo del usuario autenticado (admin o cliente)
     */
    @PutMapping("/me")
    public ResponseEntity<?> updateMyProfile(
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        try {
            User user = userRepository
                    .findByEmail(principal.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setEmail(request.getEmail());
            user.setPhone(request.getPhone());

            userRepository.save(user);

            UserProfile profile = userProfileRepository
                    .findByUser(user)
                    .orElseThrow(() -> new RuntimeException("Perfil no encontrado"));

            profile.setAddress(request.getAddress());
            profile.setCity(request.getCity());
            profile.setBirthDate(request.getBirthDate());
            profile.setDrivingLicense(request.getDrivingLicense());

            userProfileService.save(profile);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Perfil actualizado exitosamente"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", "Error interno del servidor", "details", e.getMessage()));
        }
    }
}