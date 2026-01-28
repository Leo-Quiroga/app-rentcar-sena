package com.autoreserve.backend.config;

import com.autoreserve.backend.domain.entity.*;
import com.autoreserve.backend.domain.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.Optional;

@Component
@Order(1)
@Transactional
public class DataInitializer implements CommandLineRunner {

    @Autowired private RoleRepository roleRepository;
    @Autowired private CarRepository carRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private BranchRepository branchRepository;

    @Override
    public void run(String... args) throws Exception {
        // Insertar roles si no existen (idempotente)
        if (roleRepository.count() == 0) {
            if (roleRepository.findByName("CLIENTE").isEmpty()) {
                Role cliente = new Role();
                cliente.setId(1L);
                cliente.setName("CLIENTE");
                roleRepository.save(cliente);
            }
            if (roleRepository.findByName("ADMIN").isEmpty()) {
                Role admin = new Role();
                admin.setId(2L);
                admin.setName("ADMIN");
                roleRepository.save(admin);
            }
            System.out.println(">> Roles CLIENTE y ADMIN creados (si no existían).");
        }

        // Crear branch y category si no existen
        if (categoryRepository.count() == 0 || branchRepository.count() == 0) {
            Branch branch = new Branch();
            branch.setName("Sede Principal");
            branchRepository.save(branch);

            Category category = new Category();
            category.setName("Sedán");
            categoryRepository.save(category);

            System.out.println(">> Branch y Category creados (si no existían).");

            // Crear un coche de prueba de forma segura e idempotente
            if (carRepository.count() == 0) {
                Car car = new Car();
                car.setBrand("Toyota");
                car.setModel("Corolla");
                car.setYear(2024);
                car.setPlate("ABC-123");
                car.setPricePerDay(new BigDecimal("150.00"));
                car.setStatus(CarStatus.AVAILABLE);
                car.setBranch(branch);
                car.setCategory(category);
                carRepository.save(car);
                System.out.println(">> Auto de prueba creado correctamente.");
            }
        }
    }
}