package com.autoreserve.backend.web.controller;

import com.autoreserve.backend.domain.entity.*;
import com.autoreserve.backend.domain.repository.*;
import com.autoreserve.backend.dto.reservation.AdminReservationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(roles = "ADMIN")
class AdminReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @MockitoBean
    private ReservationRepository reservationRepository;

    @MockitoBean
    private CarRepository carRepository;

    @MockitoBean
    private CarModelRepository carModelRepository;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private BranchRepository branchRepository;

    private Reservation testReservation;
    private User testUser;
    private CarModel testCarModel;
    private Branch testBranch;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        Role role = new Role();
        role.setId(1L);
        role.setName("CLIENT");

        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setEmail("test@example.com");
        testUser.setRole(role);

        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("SUV");

        testBranch = new Branch();
        testBranch.setId(1L);
        testBranch.setName("Sede Central");

        testCarModel = new CarModel();
        testCarModel.setId(1L);
        testCarModel.setBrand("Toyota");
        testCarModel.setModel("RAV4");
        testCarModel.setYear(2023);
        testCarModel.setPricePerDay(new BigDecimal("50.00"));
        testCarModel.setCategory(testCategory);

        testReservation = new Reservation();
        testReservation.setId(1L);
        testReservation.setUser(testUser);
        testReservation.setCarModel(testCarModel);
        testReservation.setStartDate(LocalDate.of(2025, 1, 10));
        testReservation.setEndDate(LocalDate.of(2025, 1, 15));
        testReservation.setStatus(ReservationStatus.PENDING);
        testReservation.setPaymentStatus(PaymentStatus.NO_PAYMENT);
        testReservation.setTotalAmount(new BigDecimal("250.00"));
        testReservation.setTotalDays(5);
        testReservation.setPricePerDay(new BigDecimal("50.00"));
        testReservation.setPickupBranch(testBranch);
        testReservation.setDropoffBranch(testBranch);
    }

    @Test
    void getAllReservations_ReturnsPagedReservations() throws Exception {
        when(reservationRepository.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(testReservation)));

        mockMvc.perform(get("/api/admin/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getReservationById_ExistingReservation_ReturnsReservation() throws Exception {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));

        mockMvc.perform(get("/api/admin/reservations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getReservationById_NonExisting_ReturnsBadRequest() throws Exception {
        when(reservationRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/admin/reservations/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void createReservation_ValidData_ReturnsSuccess() throws Exception {
        AdminReservationRequest request = new AdminReservationRequest();
        request.setUserId(1L);
        request.setCarId(1L);
        request.setStartDate(LocalDate.of(2025, 2, 1));
        request.setEndDate(LocalDate.of(2025, 2, 5));
        request.setPickupBranchId(1L);
        request.setDropoffBranchId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(carModelRepository.findById(1L)).thenReturn(Optional.of(testCarModel));
        when(branchRepository.findById(1L)).thenReturn(Optional.of(testBranch));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);

        mockMvc.perform(post("/api/admin/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void createReservation_InvalidDates_ReturnsBadRequest() throws Exception {
        AdminReservationRequest request = new AdminReservationRequest();
        request.setUserId(1L);
        request.setCarId(1L);
        request.setStartDate(LocalDate.of(2025, 2, 5));
        request.setEndDate(LocalDate.of(2025, 2, 1));
        request.setPickupBranchId(1L);
        request.setDropoffBranchId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(carModelRepository.findById(1L)).thenReturn(Optional.of(testCarModel));
        when(branchRepository.findById(1L)).thenReturn(Optional.of(testBranch));

        mockMvc.perform(post("/api/admin/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void updatePaymentStatus_ValidStatus_ReturnsSuccess() throws Exception {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);

        mockMvc.perform(put("/api/admin/reservations/1/payment-status")
                .param("paymentStatus", "PAID"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void updatePaymentStatus_InvalidStatus_ReturnsBadRequest() throws Exception {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));

        mockMvc.perform(put("/api/admin/reservations/1/payment-status")
                .param("paymentStatus", "INVALID_STATUS"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void cancelReservation_PendingReservation_ReturnsSuccess() throws Exception {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);

        mockMvc.perform(put("/api/admin/reservations/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void cancelReservation_AlreadyCancelled_ReturnsBadRequest() throws Exception {
        testReservation.setStatus(ReservationStatus.CANCELLED);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));

        mockMvc.perform(put("/api/admin/reservations/1/cancel"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void cancelReservation_InProgress_ReturnsBadRequest() throws Exception {
        testReservation.setStatus(ReservationStatus.IN_PROGRESS);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));

        mockMvc.perform(put("/api/admin/reservations/1/cancel"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void updateReservationStatus_InvalidStatus_ReturnsBadRequest() throws Exception {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));

        mockMvc.perform(put("/api/admin/reservations/1/status")
                .param("status", "INVALID"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void updateReservationStatus_ConfirmWithoutPayment_ReturnsBadRequest() throws Exception {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));
 
        mockMvc.perform(put("/api/admin/reservations/1/status")
                .param("status", "CONFIRMED"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void updateReservationStatus_ConfirmAssignsAvailableCar_ReturnsSuccess() throws Exception {
        testReservation.setPaymentStatus(PaymentStatus.PAID);
        testReservation.setStatus(ReservationStatus.PENDING);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));

        Car availableCar = new Car();
        availableCar.setId(99L);
        availableCar.setPlate("ABC-999");
        availableCar.setCarModel(testCarModel);
        availableCar.setStatus(CarStatus.AVAILABLE);

        when(carRepository.findAvailableUnitForModel(testCarModel.getId(), testReservation.getStartDate(), testReservation.getEndDate()))
                .thenReturn(List.of(availableCar));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/api/admin/reservations/1/status")
                .param("status", "CONFIRMED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.assignedCarId").value(99));
    }

    @Test
    void updateReservationStatus_CancelConfirmedPaid_ReleasesCarAndSetsRefundPending() throws Exception {
        Car assignedCar = new Car();
        assignedCar.setId(100L);
        assignedCar.setPlate("DEF-100");
        assignedCar.setStatus(CarStatus.RENTED);
        testReservation.setCar(assignedCar);
        testReservation.setStatus(ReservationStatus.CONFIRMED);
        testReservation.setPaymentStatus(PaymentStatus.PAID);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));
        when(carRepository.save(any(Car.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/api/admin/reservations/1/status")
                .param("status", "CANCELLED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.newStatus").value("CANCELLED"));
    }

    @Test
    void updateReservationStatus_CompleteWithAssignedCar_ReleasesCar() throws Exception {
        Car assignedCar = new Car();
        assignedCar.setId(101L);
        assignedCar.setPlate("GHI-101");
        assignedCar.setStatus(CarStatus.RENTED);
        testReservation.setCar(assignedCar);
        testReservation.setStatus(ReservationStatus.CONFIRMED);
        testReservation.setPaymentStatus(PaymentStatus.PAID);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));
        when(carRepository.save(any(Car.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/api/admin/reservations/1/status")
                .param("status", "COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.newStatus").value("COMPLETED"));
    }

    @Test
    void cancelReservation_ConfirmedPaidReservationTriggersRefundAndReleasesCar() throws Exception {
        Car assignedCar = new Car();
        assignedCar.setId(200L);
        assignedCar.setPlate("XYZ-200");
        assignedCar.setStatus(CarStatus.RENTED);
        testReservation.setStatus(ReservationStatus.CONFIRMED);
        testReservation.setPaymentStatus(PaymentStatus.PAID);
        testReservation.setCar(assignedCar);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));
        when(carRepository.save(any(Car.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/api/admin/reservations/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.paymentStatus").value("REFUND_PENDING"));
    }
}
