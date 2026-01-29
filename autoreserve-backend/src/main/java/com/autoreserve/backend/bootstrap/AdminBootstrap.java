package com.autoreserve.backend.bootstrap;

import com.autoreserve.backend.domain.entity.Role;
import com.autoreserve.backend.domain.entity.User;
import com.autoreserve.backend.domain.repository.RoleRepository;
import com.autoreserve.backend.domain.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Componente de inicialización de datos de usuario y roles.
 * Se ejecuta al iniciar la aplicación para garantizar la existencia de usuarios base.
 */
@Component
@Order(2)
public class AdminBootstrap implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Inyección de dependencias mediante constructor para repositorios y codificador de contraseñas.
     */
    public AdminBootstrap(UserRepository userRepository,
                          RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Método de ejecución de lógica de bootstrap.
     * Verifica la existencia de roles y usuarios iniciales, creándolos en caso de ausencia.
     */
    @Override
    public void run(String... args) {
        final String adminEmail = "admin@example.com";
        final String clientEmail = "client@example.com";

        // Verificación y creación de rol ADMINISTRADOR
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setName("ADMIN");
                    return roleRepository.save(r);
                });

        // Verificación y creación de rol CLIENTE
        Role clientRole = roleRepository.findByName("CLIENT")
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setName("CLIENT");
                    return roleRepository.save(r);
                });

        // Verificación y creación de usuario con privilegios de administrador
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

        // Verificación y creación de usuario con privilegios de cliente
        if (userRepository.findByEmail(clientEmail).isEmpty()) {
            User client = new User();
            client.setFirstName("Cliente");
            client.setLastName("Prueba");
            client.setEmail(clientEmail);
            client.setPasswordHash(passwordEncoder.encode("client123"));
            client.setPhone("1111111111");
            client.setRole(clientRole);
            userRepository.save(client);
            System.out.println(">> Usuario cliente creado (si no existía).");
        }
    }
}