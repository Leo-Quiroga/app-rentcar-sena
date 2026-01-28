package com.autoreserve.backend.bootstrap;

import com.autoreserve.backend.domain.entity.Role;
import com.autoreserve.backend.domain.entity.User;
import com.autoreserve.backend.domain.repository.RoleRepository;
import com.autoreserve.backend.domain.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class AdminBootstrap implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminBootstrap(UserRepository userRepository,
                          RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        final String adminEmail = "admin@example.com";

        // Si no existe el rol ADMIN, crearlo
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setId(2L);
                    r.setName("ADMIN");
                    return roleRepository.save(r);
                });

        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            User admin = new User();
            admin.setFirstName("Administrador");
            admin.setLastName("Sistema");
            admin.setEmail(adminEmail);
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            admin.setPhone("0000000000");
            admin.setRole(adminRole);
            userRepository.save(admin);
            System.out.println(">> Usuario admin creado (si no existía).");
        }
    }
}