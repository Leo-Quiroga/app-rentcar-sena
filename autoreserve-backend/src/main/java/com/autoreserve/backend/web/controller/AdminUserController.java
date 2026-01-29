package com.autoreserve.backend.web.controller;

import com.autoreserve.backend.domain.entity.Role;
import com.autoreserve.backend.domain.entity.User;
import com.autoreserve.backend.domain.repository.RoleRepository;
import com.autoreserve.backend.domain.repository.UserRepository;
import com.autoreserve.backend.dto.user.CreateUserRequest;
import com.autoreserve.backend.dto.user.PagedUserResponse;
import com.autoreserve.backend.dto.user.UpdateUserRequest;
import com.autoreserve.backend.dto.user.UserResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador de gestión administrativa de usuarios.
 * Proporciona operaciones CRUD protegidas exclusivamente para el rol ADMIN.
 */
@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserController(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Crea un nuevo usuario con el rol predeterminado CLIENT.
     */
    @PostMapping
    public ResponseEntity<UserResponse> createClient(
            @Valid @RequestBody CreateUserRequest request
    ) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        Role clientRole = roleRepository.findByName("CLIENT")
                .orElseThrow(() -> new RuntimeException("CLIENT role not found"));

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(clientRole);

        User saved = userRepository.save(user);

        return ResponseEntity.ok(
                new UserResponse(saved.getId(), saved.getEmail(), saved.getRole().getName())
        );
    }

    /**
     * Actualiza la información de un usuario existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        User updated = userRepository.save(user);

        return ResponseEntity.ok(
                new UserResponse(updated.getId(), updated.getEmail(), updated.getRole().getName())
        );
    }

    /**
     * Elimina un usuario del sistema. Impide la eliminación de cuentas con rol ADMIN.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if ("ADMIN".equals(user.getRole().getName())) {
            return ResponseEntity.badRequest()
                    .body("No se puede eliminar un usuario ADMIN");
        }

        userRepository.delete(user);
        return ResponseEntity.ok("Usuario eliminado correctamente");
    }

    /**
     * Obtiene los detalles de un usuario específico por su ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(
                new UserResponse(user.getId(), user.getEmail(), user.getRole().getName())
        );
    }

    /**
     * Lista todos los usuarios registrados con soporte para paginación.
     */
    @GetMapping
    public ResponseEntity<PagedUserResponse> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<User> userPage = userRepository.findAll(PageRequest.of(page, size));

        List<UserResponse> users = userPage.getContent()
                .stream()
                .map(user -> new UserResponse(user.getId(), user.getEmail(), user.getRole().getName()))
                .toList();

        PagedUserResponse response = new PagedUserResponse(
                users,
                userPage.getNumber(),
                userPage.getSize(),
                userPage.getTotalElements(),
                userPage.getTotalPages()
        );

        return ResponseEntity.ok(response);
    }
}