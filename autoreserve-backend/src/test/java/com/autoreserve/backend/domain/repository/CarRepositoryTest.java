package com.autoreserve.backend.domain.repository;

import com.autoreserve.backend.domain.entity.*;
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

    private Car testCar;
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

        testCar = new Car();
        testCar.setBrand("Toyota");
        testCar.setModel("RAV4");
        testCar.setYear(2023);
        testCar.setPlate("ABC123");
        testCar.setPricePerDay(new BigDecimal("50.00"));
        testCar.setStatus(CarStatus.AVAILABLE);
        testCar.setCategory(testCategory);
        testCar.setBranch(testBranch);
        entityManager.persist(testCar);
        entityManager.flush();
    }

    @Test
    void findByStatus_ReturnsAvailableCars() {
        Page<Car> result = carRepository.findByStatus(CarStatus.AVAILABLE, PageRequest.of(0, 10));
        
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getBrand()).isEqualTo("Toyota");
    }

    @Test
    void findByCategoryIdAndStatus_ReturnsFilteredCars() {
        Page<Car> result = carRepository.findByCategoryIdAndStatus(
                testCategory.getId(), CarStatus.AVAILABLE, PageRequest.of(0, 10));
        
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getCategory().getName()).isEqualTo("SUV");
    }

    @Test
    void findAvailableCars_ExcludesReservedCars() {
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
        reservation.setUser(user);
        reservation.setStartDate(LocalDate.of(2024, 1, 2));
        reservation.setEndDate(LocalDate.of(2024, 1, 4));
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation.setTotalAmount(new BigDecimal("150.00"));
        entityManager.persist(reservation);
        entityManager.flush();

        List<Car> result = carRepository.findAvailableCars(
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 5), null);
        
        assertThat(result).isEmpty();
    }
}
