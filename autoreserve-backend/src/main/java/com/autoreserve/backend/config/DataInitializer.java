package com.autoreserve.backend.config;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.autoreserve.backend.domain.entity.Branch;
import com.autoreserve.backend.domain.entity.Car;
import com.autoreserve.backend.domain.entity.CarModel;
import com.autoreserve.backend.domain.entity.CarStatus;
import com.autoreserve.backend.domain.entity.Category;
import com.autoreserve.backend.domain.repository.BranchRepository;
import com.autoreserve.backend.domain.repository.CarModelRepository;
import com.autoreserve.backend.domain.repository.CarRepository;
import com.autoreserve.backend.domain.repository.CategoryRepository;
import com.autoreserve.backend.domain.repository.RoleRepository;

/**
 * Componente de inicialización de datos maestros del sistema. Se encarga de
 * poblar la base de datos con registros iniciales de sedes, categorías y
 * vehículos.
 */
@Component
@Profile("!test")
@Order(1)
@Transactional
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private CarModelRepository carModelRepository;

    /**
     * Método principal de ejecución de la precarga de datos. Realiza
     * verificaciones de existencia para asegurar que la operación sea
     * idempotente.
     */
    @Override
    public void run(String... args) throws Exception {

        // Inicialización de registros de sedes y categorías de prueba en caso de base de datos vacía
        if (categoryRepository.count() == 0 || branchRepository.count() == 0) {
            Branch branch = new Branch();
            branch.setName("Sede Principal");
            branchRepository.save(branch);

            Category category = new Category();
            category.setName("Sedán");
            categoryRepository.save(category);

            System.out.println(">> Branch y Category creados (si no existían).");

            // Instanciación de un vehículo inicial vinculado a la sede y categoría creadas
            if (carRepository.count() == 0) {
                CarModel carModel = new CarModel();
                carModel.setBrand("Toyota");
                carModel.setModel("Corolla");
                carModel.setYear(2024);
                carModel.setPricePerDay(new BigDecimal("150.00"));
                carModel.setCategory(category);
                // Asume que CarModel tiene setImage y setDescription si son requeridos; ajusta si no
                carModel.setImage(""); // o null si permite
                carModel.setDescription("Auto de prueba");
                // Guarda el CarModel primero
                CarModel savedCarModel = carModelRepository.save(carModel); // Necesitas inyectar CarModelRepository

                Car car = new Car();
                car.setCarModel(savedCarModel);
                car.setPlate("ABC-123");
                car.setColor("Blanco"); // Agrega color si es requerido
                car.setStatus(CarStatus.AVAILABLE);
                car.setBranch(branch);
                carRepository.save(car);
                System.out.println(">> Auto de prueba creado correctamente.");
            }
        }
    }
}
