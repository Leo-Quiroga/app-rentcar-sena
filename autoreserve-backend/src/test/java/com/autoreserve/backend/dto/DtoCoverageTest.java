package com.autoreserve.backend.dto;

import com.autoreserve.backend.dto.auth.LoginRequest;
import com.autoreserve.backend.dto.auth.LoginResponse;
import com.autoreserve.backend.dto.auth.RegisterRequest;
import com.autoreserve.backend.dto.branch.BranchRequest;
import com.autoreserve.backend.dto.branch.BranchResponse;
import com.autoreserve.backend.dto.car.CarModelRequest;
import com.autoreserve.backend.dto.car.CarModelResponse;
import com.autoreserve.backend.dto.car.CarRequest;
import com.autoreserve.backend.dto.car.CarResponse;
import com.autoreserve.backend.dto.car.CarUnitResponse;
import com.autoreserve.backend.dto.car.CarUnitUpdateRequest;
import com.autoreserve.backend.dto.category.CategoryRequest;
import com.autoreserve.backend.dto.category.CategoryResponse;
import com.autoreserve.backend.dto.favorite.FavoriteResponse;
import com.autoreserve.backend.dto.profile.ProfileResponse;
import com.autoreserve.backend.dto.profile.UpdateProfileRequest;
import com.autoreserve.backend.dto.reservation.AdminReservationRequest;
import com.autoreserve.backend.dto.reservation.ReservationRequest;
import com.autoreserve.backend.dto.reservation.ReservationResponse;
import com.autoreserve.backend.dto.user.CreateUserRequest;
import com.autoreserve.backend.dto.user.PagedUserResponse;
import com.autoreserve.backend.dto.user.UpdateUserRequest;
import com.autoreserve.backend.dto.user.UserResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DtoCoverageTest {

    @Test
    void authDtoGettersAndSettersWork() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFirstName("Ana");
        registerRequest.setLastName("Gomez");
        registerRequest.setEmail("ana@example.com");
        registerRequest.setPassword("pass");
        registerRequest.setPhone("555444333");

        assertThat(registerRequest.getEmail()).isEqualTo("ana@example.com");
        assertThat(registerRequest.getPassword()).isEqualTo("pass");

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("ana@example.com");
        loginRequest.setPassword("pass");
        assertThat(loginRequest.getEmail()).isEqualTo("ana@example.com");

        LoginResponse loginResponse = new LoginResponse(1L, "ana@example.com", "CLIENT", "token");
        assertThat(loginResponse.getUserId()).isEqualTo(1L);
        assertThat(loginResponse.getToken()).isEqualTo("token");
    }

    @Test
    void categoryAndBranchDtoGettersAndSettersWork() {
        CategoryRequest categoryRequest = new CategoryRequest();
        categoryRequest.setName("VIP");
        categoryRequest.setDescription("Premium");
        categoryRequest.setImage("vip.jpg");
        assertThat(categoryRequest.getImage()).isEqualTo("vip.jpg");

        CategoryResponse categoryResponse = new CategoryResponse(1L, "VIP", "Premium", "vip.jpg", 0);
        assertThat(categoryResponse.getName()).isEqualTo("VIP");

        BranchRequest branchRequest = new BranchRequest();
        branchRequest.setName("Sucursal");
        branchRequest.setCity("Ciudad");
        assertThat(branchRequest.getCity()).isEqualTo("Ciudad");

        BranchResponse branchResponse = new BranchResponse(2L, "Sucursal", "Direccion", "Ciudad", "999", "branch.jpg", 3);
        assertThat(branchResponse.getName()).isEqualTo("Sucursal");
        assertThat(branchResponse.getCarCount()).isEqualTo(3);
    }

    @Test
    void carDtoGettersAndSettersWork() {
        CarRequest carRequest = new CarRequest();
        carRequest.setCategoryId(5L);
        carRequest.setBranchId(7L);
        assertThat(carRequest.getCategoryId()).isEqualTo(5L);

        CarResponse carResponse = new CarResponse(10L, "BMW", "X3", 2024, "ABC-123", new BigDecimal("180.00"), "AVAILABLE", "SUV", "Sucursal", "image.jpg");
        assertThat(carResponse.getBrand()).isEqualTo("BMW");
        carResponse.setImage("photo.jpg");
        assertThat(carResponse.getImage()).isEqualTo("photo.jpg");

        CarModelRequest modelRequest = new CarModelRequest();
        modelRequest.setBrand("Kia");
        modelRequest.setModel("Sportage");
        modelRequest.setYear(2025);
        modelRequest.setPricePerDay(new BigDecimal("130.00"));
        modelRequest.setCategoryId(3L);
        modelRequest.setBranchId(4L);
        modelRequest.setQuantity(2);
        assertThat(modelRequest.getYear()).isEqualTo(2025);

        CarModelResponse modelResponse = new CarModelResponse(20L, "Kia", "Sportage", 2025, new BigDecimal("130.00"), "image2.jpg", "Compacto", "SUV", 1L, 5L, 2L);
        assertThat(modelResponse.getCategoryName()).isEqualTo("SUV");
        modelResponse.setAvailableUnits(6L);
        assertThat(modelResponse.getAvailableUnits()).isEqualTo(6L);

        CarUnitResponse unitResponse = new CarUnitResponse(30L, 20L, "Kia", "Sportage", 2025, "ABC-123", "Rojo", "AVAILABLE", "Sucursal", 4L, "Notas");
        assertThat(unitResponse.getBranchName()).isEqualTo("Sucursal");

        CarUnitUpdateRequest unitUpdate = new CarUnitUpdateRequest();
        unitUpdate.setPlate("DEF-456");
        unitUpdate.setStatus("AVAILABLE");
        assertThat(unitUpdate.getStatus()).isEqualTo("AVAILABLE");
    }

    @Test
    void favoriteAndProfileDtoGettersAndSettersWork() {
        FavoriteResponse favoriteResponse = new FavoriteResponse(50L, 20L, "Nissan", "Versa", 2024, new BigDecimal("95.00"), "fav.jpg", "Económico", "Sedán", 3L, 5L, LocalDateTime.now());
        assertThat(favoriteResponse.getBrand()).isEqualTo("Nissan");
        favoriteResponse.setFavorite(false);
        assertThat(favoriteResponse.isFavorite()).isFalse();

        ProfileResponse profileResponse = new ProfileResponse("Laura", "Diaz", "laura@example.com", "123456", "Calle 1", "Ciudad", LocalDate.of(1990, 5, 8), "B123456", LocalDateTime.now());
        assertThat(profileResponse.getEmail()).isEqualTo("laura@example.com");

        UpdateProfileRequest updateProfile = new UpdateProfileRequest();
        updateProfile.setFirstName("Laura");
        updateProfile.setCity("Ciudad");
        assertThat(updateProfile.getCity()).isEqualTo("Ciudad");
    }

    @Test
    void reservationAndUserDtoGettersAndSettersWork() {
        ReservationRequest reservationRequest = new ReservationRequest();
        reservationRequest.setCarId(7L);
        reservationRequest.setPickupBranchId(2L);
        reservationRequest.setDropoffBranchId(3L);
        reservationRequest.setStartDate(LocalDate.of(2025, 8, 1));
        reservationRequest.setEndDate(LocalDate.of(2025, 8, 5));
        assertThat(reservationRequest.getCarId()).isEqualTo(7L);

        AdminReservationRequest adminReservationRequest = new AdminReservationRequest();
        adminReservationRequest.setUserId(11L);
        adminReservationRequest.setCarId(7L);
        assertThat(adminReservationRequest.getUserId()).isEqualTo(11L);

        ReservationResponse response = new ReservationResponse(1L, 2L, "Toyota", "Corolla", 2024, "img.jpg", "Sedán", LocalDate.of(2025, 8, 1), LocalDate.of(2025, 8, 5), "PENDING", "NO_PAYMENT", new BigDecimal("120.00"), 4, new BigDecimal("30.00"), "Sucursal A", "Sucursal B", 3L, "Ana", "Gomez", "ana@example.com");
        assertThat(response.getCarBrand()).isEqualTo("Toyota");
        response.setPaymentStatus("PAID");
        assertThat(response.getPaymentStatus()).isEqualTo("PAID");

        CreateUserRequest createUser = new CreateUserRequest();
        createUser.setFirstName("Diego");
        createUser.setLastName("Lopez");
        createUser.setEmail("diego@example.com");
        assertThat(createUser.getEmail()).isEqualTo("diego@example.com");

        UpdateUserRequest updateUser = new UpdateUserRequest();
        updateUser.setPhone("321321321");
        assertThat(updateUser.getPhone()).isEqualTo("321321321");

        UserResponse userResponse = new UserResponse(4L, "Diego", "Lopez", "diego@example.com", "123", "CLIENT", LocalDateTime.now());
        assertThat(userResponse.getRole()).isEqualTo("CLIENT");

        PagedUserResponse pagedUserResponse = new PagedUserResponse(List.of(userResponse), 0, 10, 1, 1);
        assertThat(pagedUserResponse.getTotalPages()).isEqualTo(1);
    }

    @Test
    void additionalDtoAccessorsExerciseAllGettersAndSetters() {
        CarResponse carResponse = new CarResponse(11L, "Audi", "A4", 2025, "PLATE-11", new BigDecimal("150.00"), "AVAILABLE", "Sedán", "Central", "image.png");
        carResponse.setBrand("Audi");
        carResponse.setModel("A4");
        carResponse.setYear(2025);
        carResponse.setPlate("PLATE-11");
        carResponse.setPricePerDay(new BigDecimal("150.00"));
        carResponse.setStatus("AVAILABLE");
        carResponse.setCategoryName("Sedán");
        carResponse.setBranchName("Central");
        carResponse.setImage("image.png");
        assertThat(carResponse.getBrand()).isEqualTo("Audi");
        assertThat(carResponse.getBranchName()).isEqualTo("Central");

        CarModelResponse modelResponse = new CarModelResponse(21L, "Audi", "A4", 2025, new BigDecimal("150.00"), "image.png", "Premium", "Sedán", 3L, 12L, 5L);
        modelResponse.setDescription("Premium");
        modelResponse.setCategoryId(3L);
        modelResponse.setAvailableUnits(6L);
        modelResponse.setTotalUnits(10L);
        assertThat(modelResponse.getDescription()).isEqualTo("Premium");
        assertThat(modelResponse.getTotalUnits()).isEqualTo(10L);

        CarUnitResponse unitResponse = new CarUnitResponse(31L, 21L, "Audi", "A4", 2025, "PLATE-31", "Negro", "AVAILABLE", "Central", 7L, "Sin observaciones");
        unitResponse.setCarModelId(21L);
        unitResponse.setColor("Negro");
        unitResponse.setBranchId(7L);
        assertThat(unitResponse.getCarModelId()).isEqualTo(21L);
        assertThat(unitResponse.getBranchId()).isEqualTo(7L);

        FavoriteResponse favoriteResponse = new FavoriteResponse();
        favoriteResponse.setFavoriteId(51L);
        favoriteResponse.setCarModelId(31L);
        favoriteResponse.setBrand("Kia");
        favoriteResponse.setModel("Sportage");
        favoriteResponse.setYear(2024);
        favoriteResponse.setPricePerDay(new BigDecimal("90.00"));
        favoriteResponse.setImage("fav.png");
        favoriteResponse.setDescription("Compacto");
        favoriteResponse.setCategoryName("SUV");
        favoriteResponse.setAvailableUnits(4L);
        favoriteResponse.setTotalUnits(8L);
        favoriteResponse.setAddedAt(LocalDateTime.now());
        favoriteResponse.setFavorite(true);
        assertThat(favoriteResponse.getModel()).isEqualTo("Sportage");
        assertThat(favoriteResponse.isFavorite()).isTrue();

        ProfileResponse profileResponse = new ProfileResponse("Laura", "Diaz", "laura@example.com", "123456", "Calle 1", "Ciudad", LocalDate.of(1990, 5, 8), "B123456", LocalDateTime.now());
        assertThat(profileResponse.getDrivingLicense()).isEqualTo("B123456");
        assertThat(profileResponse.getCreatedAt()).isNotNull();

        UpdateProfileRequest updateProfile = new UpdateProfileRequest();
        updateProfile.setFirstName("Laura");
        updateProfile.setLastName("Perez");
        updateProfile.setEmail("laura.perez@example.com");
        updateProfile.setPhone("999888777");
        updateProfile.setAddress("Calle 2");
        updateProfile.setCity("Ciudad");
        updateProfile.setBirthDate(LocalDate.of(1990, 5, 8));
        updateProfile.setDrivingLicense("B123456");
        assertThat(updateProfile.getAddress()).isEqualTo("Calle 2");
        assertThat(updateProfile.getEmail()).isEqualTo("laura.perez@example.com");
    }

    @Test
    void carRequestAllFields() {
        CarRequest carRequest = new CarRequest();
        carRequest.setBrand("Toyota");
        carRequest.setModel("Corolla");
        carRequest.setYear(2024);
        carRequest.setPlate("ABC-123");
        carRequest.setPricePerDay(new BigDecimal("50.00"));
        carRequest.setCategoryId(1L);
        carRequest.setBranchId(2L);
        carRequest.setImage("corolla.jpg");
        assertThat(carRequest.getBrand()).isEqualTo("Toyota");
        assertThat(carRequest.getModel()).isEqualTo("Corolla");
        assertThat(carRequest.getYear()).isEqualTo(2024);
        assertThat(carRequest.getPlate()).isEqualTo("ABC-123");
        assertThat(carRequest.getPricePerDay()).isEqualTo(new BigDecimal("50.00"));
        assertThat(carRequest.getCategoryId()).isEqualTo(1L);
        assertThat(carRequest.getBranchId()).isEqualTo(2L);
        assertThat(carRequest.getImage()).isEqualTo("corolla.jpg");
    }

    @Test
    void carResponseAllFields() {
        CarResponse carResponse = new CarResponse(1L, "Honda", "Civic", 2023, "XYZ-789", new BigDecimal("45.00"), "RENTED", "Sedán", "Sucursal Norte", "civic.jpg");
        assertThat(carResponse.getId()).isEqualTo(1L);
        assertThat(carResponse.getBrand()).isEqualTo("Honda");
        assertThat(carResponse.getModel()).isEqualTo("Civic");
        assertThat(carResponse.getYear()).isEqualTo(2023);
        assertThat(carResponse.getPlate()).isEqualTo("XYZ-789");
        assertThat(carResponse.getPricePerDay()).isEqualTo(new BigDecimal("45.00"));
        assertThat(carResponse.getStatus()).isEqualTo("RENTED");
        assertThat(carResponse.getCategoryName()).isEqualTo("Sedán");
        assertThat(carResponse.getBranchName()).isEqualTo("Sucursal Norte");
        assertThat(carResponse.getImage()).isEqualTo("civic.jpg");
    }

    @Test
    void carUnitResponseAllFields() {
        CarUnitResponse unitResponse = new CarUnitResponse(1L, 10L, "Ford", "Focus", 2022, "DEF-456", "Blanco", "MAINTENANCE", "Sucursal Sur", 3L, "Revisión pendiente");
        assertThat(unitResponse.getId()).isEqualTo(1L);
        assertThat(unitResponse.getCarModelId()).isEqualTo(10L);
        assertThat(unitResponse.getBrand()).isEqualTo("Ford");
        assertThat(unitResponse.getModel()).isEqualTo("Focus");
        assertThat(unitResponse.getYear()).isEqualTo(2022);
        assertThat(unitResponse.getPlate()).isEqualTo("DEF-456");
        assertThat(unitResponse.getColor()).isEqualTo("Blanco");
        assertThat(unitResponse.getStatus()).isEqualTo("MAINTENANCE");
        assertThat(unitResponse.getBranchName()).isEqualTo("Sucursal Sur");
        assertThat(unitResponse.getBranchId()).isEqualTo(3L);
        assertThat(unitResponse.getNotes()).isEqualTo("Revisión pendiente");
    }

    @Test
    void carModelResponseAllFields() {
        CarModelResponse modelResponse = new CarModelResponse(1L, "Chevrolet", "Malibu", 2024, new BigDecimal("70.00"), "malibu.jpg", "Sedán familiar", "Sedán", 2L, 8L, 15L);
        assertThat(modelResponse.getId()).isEqualTo(1L);
        assertThat(modelResponse.getBrand()).isEqualTo("Chevrolet");
        assertThat(modelResponse.getModel()).isEqualTo("Malibu");
        assertThat(modelResponse.getYear()).isEqualTo(2024);
        assertThat(modelResponse.getPricePerDay()).isEqualTo(new BigDecimal("70.00"));
        assertThat(modelResponse.getImage()).isEqualTo("malibu.jpg");
        assertThat(modelResponse.getDescription()).isEqualTo("Sedán familiar");
        assertThat(modelResponse.getCategoryName()).isEqualTo("Sedán");
        assertThat(modelResponse.getCategoryId()).isEqualTo(2L);
        assertThat(modelResponse.getAvailableUnits()).isEqualTo(8L);
        assertThat(modelResponse.getTotalUnits()).isEqualTo(15L);
    }

    @Test
    void reservationResponseAllFields() {
        ReservationResponse response = new ReservationResponse(1L, 5L, "Nissan", "Leaf", 2024, "leaf.jpg", "Eléctrico", LocalDate.of(2025, 9, 1), LocalDate.of(2025, 9, 10), "COMPLETED", "PAID", new BigDecimal("450.00"), 9, new BigDecimal("50.00"), "Sucursal Centro", "Sucursal Aeropuerto", 10L, "Carlos", "Ruiz", "carlos@example.com");
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getCarId()).isEqualTo(5L);
        assertThat(response.getCarBrand()).isEqualTo("Nissan");
        assertThat(response.getCarModel()).isEqualTo("Leaf");
        assertThat(response.getCarYear()).isEqualTo(2024);
        assertThat(response.getCarImage()).isEqualTo("leaf.jpg");
        assertThat(response.getCategoryName()).isEqualTo("Eléctrico");
        assertThat(response.getStartDate()).isEqualTo(LocalDate.of(2025, 9, 1));
        assertThat(response.getEndDate()).isEqualTo(LocalDate.of(2025, 9, 10));
        assertThat(response.getStatus()).isEqualTo("COMPLETED");
        assertThat(response.getPaymentStatus()).isEqualTo("PAID");
        assertThat(response.getTotalAmount()).isEqualTo(new BigDecimal("450.00"));
        assertThat(response.getTotalDays()).isEqualTo(9);
        assertThat(response.getPricePerDay()).isEqualTo(new BigDecimal("50.00"));
        assertThat(response.getPickupBranchName()).isEqualTo("Sucursal Centro");
        assertThat(response.getDropoffBranchName()).isEqualTo("Sucursal Aeropuerto");
        assertThat(response.getUserId()).isEqualTo(10L);
        assertThat(response.getUserFirstName()).isEqualTo("Carlos");
        assertThat(response.getUserLastName()).isEqualTo("Ruiz");
        assertThat(response.getUserEmail()).isEqualTo("carlos@example.com");
    }

    @Test
    void favoriteResponseAllFields() {
        FavoriteResponse favorite = new FavoriteResponse(1L, 20L, "Mazda", "CX-5", 2023, new BigDecimal("85.00"), "cx5.jpg", "SUV deportivo", "SUV", 12L, 20L, LocalDateTime.of(2025, 8, 10, 14, 30));
        assertThat(favorite.getFavoriteId()).isEqualTo(1L);
        assertThat(favorite.getCarModelId()).isEqualTo(20L);
        assertThat(favorite.getBrand()).isEqualTo("Mazda");
        assertThat(favorite.getModel()).isEqualTo("CX-5");
        assertThat(favorite.getYear()).isEqualTo(2023);
        assertThat(favorite.getPricePerDay()).isEqualTo(new BigDecimal("85.00"));
        assertThat(favorite.getImage()).isEqualTo("cx5.jpg");
        assertThat(favorite.getDescription()).isEqualTo("SUV deportivo");
        assertThat(favorite.getCategoryName()).isEqualTo("SUV");
        assertThat(favorite.getAvailableUnits()).isEqualTo(12L);
        assertThat(favorite.getTotalUnits()).isEqualTo(20L);
        assertThat(favorite.getAddedAt()).isNotNull();
        assertThat(favorite.isFavorite()).isTrue();
    }

    @Test
    void branchResponseAllFields() {
        BranchResponse branch = new BranchResponse(1L, "Sucursal Bogotá", "Calle 100 #15-20", "Bogotá", "6012345678", "bogota.jpg", 25);
        assertThat(branch.getId()).isEqualTo(1L);
        assertThat(branch.getName()).isEqualTo("Sucursal Bogotá");
        assertThat(branch.getAddress()).isEqualTo("Calle 100 #15-20");
        assertThat(branch.getCity()).isEqualTo("Bogotá");
        assertThat(branch.getPhone()).isEqualTo("6012345678");
        assertThat(branch.getImage()).isEqualTo("bogota.jpg");
        assertThat(branch.getCarCount()).isEqualTo(25);
    }

    @Test
    void categoryResponseAllFields() {
        CategoryResponse category = new CategoryResponse(1L, "Lujo", "Vehículos de alta gama", "lujo.jpg", 8);
        assertThat(category.getId()).isEqualTo(1L);
        assertThat(category.getName()).isEqualTo("Lujo");
        assertThat(category.getDescription()).isEqualTo("Vehículos de alta gama");
        assertThat(category.getImage()).isEqualTo("lujo.jpg");
        assertThat(category.getCarCount()).isEqualTo(8);
    }

    @Test
    void reservationRequestAllFields() {
        ReservationRequest request = new ReservationRequest();
        request.setCarId(5L);
        request.setPickupBranchId(2L);
        request.setDropoffBranchId(3L);
        request.setStartDate(LocalDate.of(2025, 9, 1));
        request.setEndDate(LocalDate.of(2025, 9, 10));
        assertThat(request.getCarId()).isEqualTo(5L);
        assertThat(request.getPickupBranchId()).isEqualTo(2L);
        assertThat(request.getDropoffBranchId()).isEqualTo(3L);
        assertThat(request.getStartDate()).isEqualTo(LocalDate.of(2025, 9, 1));
        assertThat(request.getEndDate()).isEqualTo(LocalDate.of(2025, 9, 10));
    }

    @Test
    void adminReservationRequestAllFields() {
        AdminReservationRequest request = new AdminReservationRequest();
        request.setUserId(10L);
        request.setCarId(5L);
        request.setStartDate(LocalDate.of(2025, 9, 1));
        request.setEndDate(LocalDate.of(2025, 9, 10));
        request.setPickupBranchId(2L);
        request.setDropoffBranchId(3L);
        assertThat(request.getUserId()).isEqualTo(10L);
        assertThat(request.getCarId()).isEqualTo(5L);
        assertThat(request.getStartDate()).isEqualTo(LocalDate.of(2025, 9, 1));
        assertThat(request.getEndDate()).isEqualTo(LocalDate.of(2025, 9, 10));
        assertThat(request.getPickupBranchId()).isEqualTo(2L);
        assertThat(request.getDropoffBranchId()).isEqualTo(3L);
    }

    @Test
    void reservationResponseSettersWork() {
        ReservationResponse response = new ReservationResponse(
            1L, 2L, "Toyota", "Corolla", 2024, "img.jpg", "Sedán", 
            LocalDate.of(2025, 8, 1), LocalDate.of(2025, 8, 5), 
            "PENDING", "NO_PAYMENT", new BigDecimal("120.00"), 4, 
            new BigDecimal("30.00"), "Sucursal A", "Sucursal B", 
            3L, "Ana", "Gomez", "ana@example.com");
        
        // Probar todos los setters
        response.setId(2L);
        response.setCarId(3L);
        response.setCarBrand("Honda");
        response.setCarModel("Civic");
        response.setStartDate(LocalDate.of(2025, 9, 1));
        response.setEndDate(LocalDate.of(2025, 9, 10));
        response.setStatus("CONFIRMED");
        response.setTotalAmount(new BigDecimal("200.00"));
        response.setPickupBranchName("Sucursal Centro");
        response.setDropoffBranchName("Sucursal Aeropuerto");
        response.setTotalDays(9);
        response.setPricePerDay(new BigDecimal("22.22"));
        response.setCarImage("new-image.jpg");
        response.setCarYear(2025);
        response.setCategoryName("Premium");
        response.setUserId(4L);
        response.setUserFirstName("Carlos");
        response.setUserLastName("Ruiz");
        response.setUserEmail("carlos@example.com");
        
        // Verificar que los setters funcionaron
        assertThat(response.getId()).isEqualTo(2L);
        assertThat(response.getCarId()).isEqualTo(3L);
        assertThat(response.getCarBrand()).isEqualTo("Honda");
        assertThat(response.getCarModel()).isEqualTo("Civic");
        assertThat(response.getStartDate()).isEqualTo(LocalDate.of(2025, 9, 1));
        assertThat(response.getEndDate()).isEqualTo(LocalDate.of(2025, 9, 10));
        assertThat(response.getStatus()).isEqualTo("CONFIRMED");
        assertThat(response.getTotalAmount()).isEqualTo(new BigDecimal("200.00"));
        assertThat(response.getPickupBranchName()).isEqualTo("Sucursal Centro");
        assertThat(response.getDropoffBranchName()).isEqualTo("Sucursal Aeropuerto");
        assertThat(response.getTotalDays()).isEqualTo(9);
        assertThat(response.getPricePerDay()).isEqualTo(new BigDecimal("22.22"));
        assertThat(response.getCarImage()).isEqualTo("new-image.jpg");
        assertThat(response.getCarYear()).isEqualTo(2025);
        assertThat(response.getCategoryName()).isEqualTo("Premium");
        assertThat(response.getUserId()).isEqualTo(4L);
        assertThat(response.getUserFirstName()).isEqualTo("Carlos");
        assertThat(response.getUserLastName()).isEqualTo("Ruiz");
        assertThat(response.getUserEmail()).isEqualTo("carlos@example.com");
    }

    @Test
    void branchResponseSettersWork() {
        BranchResponse branch = new BranchResponse(1L, "Sucursal", "Direccion", 
            "Ciudad", "999", "branch.jpg", 3);
        
        // Probar todos los setters
        branch.setId(2L);
        branch.setName("Nueva Sucursal");
        branch.setAddress("Nueva Dirección");
        branch.setCity("Nueva Ciudad");
        branch.setPhone("111222333");
        branch.setImage("new-branch.jpg");
        branch.setCarCount(10);
        
        // Verificar que los setters funcionaron
        assertThat(branch.getId()).isEqualTo(2L);
        assertThat(branch.getName()).isEqualTo("Nueva Sucursal");
        assertThat(branch.getAddress()).isEqualTo("Nueva Dirección");
        assertThat(branch.getCity()).isEqualTo("Nueva Ciudad");
        assertThat(branch.getPhone()).isEqualTo("111222333");
        assertThat(branch.getImage()).isEqualTo("new-branch.jpg");
        assertThat(branch.getCarCount()).isEqualTo(10);
    }

    @Test
    void categoryResponseSettersWork() {
        CategoryResponse category = new CategoryResponse(1L, "VIP", "Premium", 
            "vip.jpg", 0);
        
        // Probar todos los setters
        category.setId(2L);
        category.setName("Lujo");
        category.setDescription("Vehículos de alta gama");
        category.setImage("lujo.jpg");
        category.setCarCount(8);
        
        // Verificar que los setters funcionaron
        assertThat(category.getId()).isEqualTo(2L);
        assertThat(category.getName()).isEqualTo("Lujo");
        assertThat(category.getDescription()).isEqualTo("Vehículos de alta gama");
        assertThat(category.getImage()).isEqualTo("lujo.jpg");
        assertThat(category.getCarCount()).isEqualTo(8);
    }
}
