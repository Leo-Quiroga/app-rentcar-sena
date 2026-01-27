//package com.autoreserve.backend.config;
//
//import com.autoreserve.backend.domain.entity.*;
//import com.autoreserve.backend.domain.repository.*;
//
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//
//@Configuration
//public class DataTestRunner {
//
//    @Bean
//    CommandLineRunner runTests(
//            RoleRepository roleRepository,
//            UserRepository userRepository,
//            UserProfileRepository userProfileRepository,
//            CategoryRepository categoryRepository,
//            BranchRepository branchRepository,
//            CarRepository carRepository,
//            ReservationRepository reservationRepository
//    ) {
//        return args -> {
//
//            System.out.println("========== EV01 BACKEND TEST START ==========");
//
//            /* =====================
//               ROLE
//            ====================== */
//            Role adminRole = new Role();
//            adminRole.setName("ADMIN");
//            roleRepository.save(adminRole);
//
//            /* =====================
//               USER
//            ====================== */
//            User user = new User();
//            user.setFirstName("Leonardo");
//            user.setLastName("Beltran");
//            user.setEmail("leo@test.com");
//            user.setPasswordHash("1234");
//            user.setPhone("3000000000");
//            user.setRole(adminRole);
//            userRepository.save(user);
//
//            /* =====================
//               USER PROFILE (1:1)
//            ====================== */
//            UserProfile profile = new UserProfile();
//            profile.setUser(user);
//            profile.setAddress("Main Street 123");
//            profile.setCity("Bogotá");
//            profile.setBirthDate(LocalDate.of(1995, 5, 10));
//            profile.setDrivingLicense("ABC-12345");
//            userProfileRepository.save(profile);
//
//            /* =====================
//               CATEGORY
//            ====================== */
//            Category category = new Category();
//            category.setName("SUV");
//            category.setDescription("Sport Utility Vehicle");
//            categoryRepository.save(category);
//
//            /* =====================
//               BRANCH
//            ====================== */
//            Branch branch = new Branch();
//            branch.setName("Central Branch");
//            branch.setCity("Bogotá");
//            branch.setAddress("Airport Avenue");
//            branchRepository.save(branch);
//
//            /* =====================
//               CAR
//            ====================== */
//            Car car = new Car();
//            car.setBrand("Toyota");
//            car.setModel("RAV4");
//            car.setYear(2023);
//            car.setPlate("ABC123");
//            car.setPricePerDay(new BigDecimal("250000"));
//            car.setStatus(CarStatus.AVAILABLE);
//            car.setCategory(category);
//            car.setBranch(branch);
//            carRepository.save(car);
//
//            /* =====================
//               RESERVATION
//            ====================== */
//            Reservation reservation = new Reservation();
//            reservation.setUser(user);
//            reservation.setCar(car);
//            reservation.setStartDate(LocalDate.now());
//            reservation.setEndDate(LocalDate.now().plusDays(3));
//            reservation.setStatus(ReservationStatus.CONFIRMED);
//            reservation.setTotalAmount(new BigDecimal("750000"));
//            reservationRepository.save(reservation);
//
//            /* =====================
//               READ TEST
//            ====================== */
//            System.out.println("Users in DB: " + userRepository.count());
//            System.out.println("Cars in DB: " + carRepository.count());
//            System.out.println("Reservations in DB: " + reservationRepository.count());
//
//            /* =====================
//               UPDATE TEST
//            ====================== */
//            car.setStatus(CarStatus.RESERVED);
//            carRepository.save(car);
//
//            /* =====================
//               DELETE TEST
//            ====================== */
//            // reservationRepository.delete(reservation);
//
//            System.out.println("========== EV01 BACKEND TEST FINISHED ==========");
//        };
//    }
//}
//
