package com.autoreserve.backend.domain.repository;

import com.autoreserve.backend.domain.entity.*;
import com.autoreserve.backend.util.TestImageUrls;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CarRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CarModelRepository carModelRepository;

    private Car testCar;
    private CarModel testCarModel;
    private Category testCategory;
    private Branch testBranch;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setName("SUV");
        testCategory.setDescription("Sport Utility Vehicle");
        entityManager.persist(testCategory);

        testBranch = new Branch();
        testBranch.setName("Sede Central");
        testBranch.setAddress("Av. Principal 123");
        testBranch.setCity("Lima");
        testBranch.setPhone("987654321");
        entityManager.persist(testBranch);

        // Crear CarModel primero
        testCarModel = new CarModel();
        testCarModel.setBrand("Toyota");
        testCarModel.setModel("RAV4");
        testCarModel.setYear(2023);
        testCarModel.setPricePerDay(new BigDecimal("50.00"));
        testCarModel.setCategory(testCategory);
        testCarModel.setDescription("Spacious and reliable SUV perfect for family trips and city driving");
        testCarModel.setImage(TestImageUrls.getImageUrl("Toyota", "RAV4"));
        entityManager.persist(testCarModel);

        // Crear Car asociado al CarModel
        testCar = new Car();
        testCar.setCarModel(testCarModel);
        testCar.setPlate("ABC123");
        testCar.setColor("Rojo");
        testCar.setStatus(CarStatus.AVAILABLE);
        testCar.setBranch(testBranch);
        entityManager.persist(testCar);
        entityManager.flush();
    }

    @Test
    void findByStatus_ReturnsAvailableCars() {
        Page<Car> result = carRepository.findByStatus(CarStatus.AVAILABLE, PageRequest.of(0, 10));
        
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getBrand()).isEqualTo("Toyota");
        assertThat(result.getContent().get(0).getStatus()).isEqualTo(CarStatus.AVAILABLE);
    }

    @Test
    void findByCarModelId_ReturnsCarsByModel() {
        List<Car> result = carRepository.findByCarModelId(testCarModel.getId());
        
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCarModel().getBrand()).isEqualTo("Toyota");
        assertThat(result.get(0).getCarModel().getModel()).isEqualTo("RAV4");
    }

    @Test
    void findAvailableUnitForModel_ExcludesReservedCars() {
        Role role = new Role();
        role.setName("CLIENT");
        entityManager.persist(role);

        User user = new User();
        user.setEmail("test@example.com");
        user.setPasswordHash("hash");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setRole(role);
        entityManager.persist(user);

        Reservation reservation = new Reservation();
        reservation.setCar(testCar);
        reservation.setCarModel(testCarModel);  // Agregar carModel requerido
        reservation.setUser(user);
        reservation.setStartDate(LocalDate.of(2024, 1, 2));
        reservation.setEndDate(LocalDate.of(2024, 1, 4));
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation.setTotalAmount(new BigDecimal("150.00"));
        entityManager.persist(reservation);
        entityManager.flush();

        // Buscar unidades disponibles para el modelo en fechas que se solapan
        List<Car> result = carRepository.findAvailableUnitForModel(
                testCarModel.getId(),
                LocalDate.of(2024, 1, 1), 
                LocalDate.of(2024, 1, 5)
        );
        
        // Debería estar vacío porque el auto está reservado en esas fechas
        assertThat(result).isEmpty();
    }

    @Test
    void countAvailableByModel_ReturnsCorrectCount() {
        long count = carRepository.countAvailableByModel(testCarModel.getId());
        assertThat(count).isEqualTo(1);
        
        // Cambiar estado a mantenimiento
        testCar.setStatus(CarStatus.MAINTENANCE);
        entityManager.merge(testCar);
        entityManager.flush();
        
        count = carRepository.countAvailableByModel(testCarModel.getId());
        assertThat(count).isEqualTo(0);
    }

    @Test
    void findByPlate_ReturnsCarWithPlate() {
        var result = carRepository.findByPlate("ABC123");
        assertThat(result).isPresent();
        assertThat(result.get().getBrand()).isEqualTo("Toyota");
    }
}
