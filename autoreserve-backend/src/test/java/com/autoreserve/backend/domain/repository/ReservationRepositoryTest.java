package com.autoreserve.backend.domain.repository;

import com.autoreserve.backend.domain.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ReservationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ReservationRepository reservationRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setName("CLIENT");
        entityManager.persist(role);

        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("hash");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setRole(role);
        entityManager.persist(testUser);

        Category category = new Category();
        category.setName("SUV");
        entityManager.persist(category);

        Branch branch = new Branch();
        branch.setName("Sede Central");
        branch.setAddress("Av. Principal 123");
        branch.setCity("Lima");
        branch.setPhone("987654321");
        entityManager.persist(branch);

        Car car = new Car();
        car.setBrand("Toyota");
        car.setModel("RAV4");
        car.setYear(2023);
        car.setPlate("ABC123");
        car.setPricePerDay(new BigDecimal("50.00"));
        car.setStatus(CarStatus.AVAILABLE);
        car.setCategory(category);
        car.setBranch(branch);
        entityManager.persist(car);

        Reservation r1 = new Reservation();
        r1.setCar(car);
        r1.setUser(testUser);
        r1.setStartDate(LocalDate.of(2024, 2, 1));
        r1.setEndDate(LocalDate.of(2024, 2, 5));
        r1.setStatus(ReservationStatus.CONFIRMED);
        r1.setTotalAmount(new BigDecimal("200.00"));
        entityManager.persist(r1);

        Reservation r2 = new Reservation();
        r2.setCar(car);
        r2.setUser(testUser);
        r2.setStartDate(LocalDate.of(2024, 1, 1));
        r2.setEndDate(LocalDate.of(2024, 1, 5));
        r2.setStatus(ReservationStatus.CONFIRMED);
        r2.setTotalAmount(new BigDecimal("200.00"));
        entityManager.persist(r2);
        entityManager.flush();
    }

    @Test
    void findByUserOrderByStartDateDesc_ReturnsOrderedReservations() {
        List<Reservation> result = reservationRepository.findByUserOrderByStartDateDesc(testUser);
        
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getStartDate()).isAfter(result.get(1).getStartDate());
    }
}
