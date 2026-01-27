package com.autoreserve.backend.config;

import com.autoreserve.backend.domain.entity.*;
import com.autoreserve.backend.domain.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private RoleRepository roleRepository;
    @Autowired private CarRepository carRepository;
    @Autowired private CategoryRepository categoryRepository; // Asegúrate de tener este Repo
    @Autowired private BranchRepository branchRepository;     // Asegúrate de tener este Repo

    @Override
    public void run(String... args) throws Exception {
        // Solo insertamos si la tabla está vacía
        if (roleRepository.count() == 0) {
            // 1. CREAR ROLES
            Role cliente = new Role();
            cliente.setId(1L); // Forzamos el ID 1 para que coincida con tu JSP
            cliente.setName("CLIENTE");
            roleRepository.save(cliente);

            Role admin = new Role();
            admin.setId(2L); // Forzamos el ID 2
            admin.setName("ADMIN");
            roleRepository.save(admin);

            System.out.println(">> Base de datos inicializada: Roles CLIENTE y ADMIN creados.");
        }
        // 2. CREAR DEPENDENCIAS DE CARRO (Branch y Category)
        // Verificamos si hay categorías, si no, creamos las necesarias para que el Carro no falle
        if (categoryRepository.count() == 0) {
            Branch branch = new Branch();
            branch.setName("Sede Principal"); // Ajusta según tu Entity Branch
            branchRepository.save(branch);

            Category category = new Category();
            category.setName("Sedán"); // Ajusta según tu Entity Category
            categoryRepository.save(category);

            // 3. CREAR CARRO POR DEFECTO
            if (carRepository.count() == 0) {
                Car car = new Car();
                car.setBrand("Toyota");
                car.setModel("Corolla");
                car.setYear(2024);
                car.setPlate("ABC-123");
                car.setPricePerDay(new BigDecimal("150.00"));
                car.setStatus(CarStatus.AVAILABLE); // Asegúrate de tener este Enum

                // Asignamos las relaciones obligatorias
                car.setBranch(branch);
                car.setCategory(category);

                carRepository.save(car);
                System.out.println(">> Auto de prueba creado correctamente.");
            }

        System.out.println(">> Base de datos inicializada: Branch y Category creados.");}
    }
}

