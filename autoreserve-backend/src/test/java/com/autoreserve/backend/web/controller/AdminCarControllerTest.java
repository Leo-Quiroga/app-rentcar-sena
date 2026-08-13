package com.autoreserve.backend.web.controller;

import com.autoreserve.backend.domain.entity.*;
import com.autoreserve.backend.domain.repository.*;
import com.autoreserve.backend.dto.car.CarModelRequest;
import com.autoreserve.backend.dto.car.CarUnitUpdateRequest;
import com.autoreserve.backend.util.TestImageUrls;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
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
class AdminCarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CarRepository carRepository;

    @MockitoBean
    private CarModelRepository carModelRepository;

    @MockitoBean
    private CategoryRepository categoryRepository;

    @MockitoBean
    private BranchRepository branchRepository;

    private CarModel testCarModel;
    private Category testCategory;
    private Branch testBranch;
    private Car testCar;

    @BeforeEach
    void setUp() {
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
        testCarModel.setDescription("SUV familiar");
        testCarModel.setImage(TestImageUrls.TOYOTA_RAV4);

        testCar = new Car();
        testCar.setId(1L);
        testCar.setCarModel(testCarModel);
        testCar.setPlate("ABC123");
        testCar.setColor("Rojo");
        testCar.setStatus(CarStatus.AVAILABLE);
        testCar.setBranch(testBranch);
    }

    @Test
    void getAllModels_ReturnsAllModels() throws Exception {
        when(carModelRepository.findAll()).thenReturn(List.of(testCarModel));
        when(carRepository.countAvailableByModel(1L)).thenReturn(2L);
        when(carRepository.countByCarModelId(1L)).thenReturn(3L);
        when(carRepository.findByCarModelId(1L)).thenReturn(List.of(testCar));

        mockMvc.perform(get("/api/admin/cars/models"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].brand").value("Toyota"))
                .andExpect(jsonPath("$[0].model").value("RAV4"));
    }

    @Test
    void getModelById_ExistingModel_ReturnsModel() throws Exception {
        when(carModelRepository.findById(1L)).thenReturn(Optional.of(testCarModel));
        when(carRepository.countAvailableByModel(1L)).thenReturn(2L);
        when(carRepository.countByCarModelId(1L)).thenReturn(3L);
        when(carRepository.findByCarModelId(1L)).thenReturn(List.of(testCar));

        mockMvc.perform(get("/api/admin/cars/models/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brand").value("Toyota"));
    }

    @Test
    void getModelById_NonExisting_ReturnsBadRequest() throws Exception {
        when(carModelRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/admin/cars/models/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void createModel_ValidData_ReturnsSuccess() throws Exception {
        CarModelRequest request = new CarModelRequest();
        request.setBrand("Honda");
        request.setModel("Civic");
        request.setYear(2023);
        request.setPricePerDay(new BigDecimal("40.00"));
        request.setCategoryId(1L);
        request.setBranchId(1L);
        request.setQuantity(2);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(branchRepository.findById(1L)).thenReturn(Optional.of(testBranch));
        when(carModelRepository.save(any(CarModel.class))).thenAnswer(inv -> {
            CarModel m = inv.getArgument(0);
            m.setId(2L);
            m.setCategory(testCategory);
            return m;
        });
        when(carRepository.save(any(Car.class))).thenReturn(testCar);

        mockMvc.perform(post("/api/admin/cars/models")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void createModel_CategoryNotFound_ReturnsBadRequest() throws Exception {
        CarModelRequest request = new CarModelRequest();
        request.setBrand("Honda");
        request.setModel("Civic");
        request.setYear(2023);
        request.setPricePerDay(new BigDecimal("40.00"));
        request.setCategoryId(999L);
        request.setBranchId(1L);
        request.setQuantity(1);

        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/admin/cars/models")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void createModel_BranchNotFound_ReturnsBadRequest() throws Exception {
        CarModelRequest request = new CarModelRequest();
        request.setBrand("Honda");
        request.setModel("Civic");
        request.setYear(2023);
        request.setPricePerDay(new BigDecimal("40.00"));
        request.setCategoryId(1L);
        request.setBranchId(999L);
        request.setQuantity(1);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(branchRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/admin/cars/models")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void addUnitsToModel_ModelNotFound_ReturnsBadRequest() throws Exception {
        when(carModelRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/admin/cars/models/999/units")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"branchId\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void addUnitsToModel_ModelWithoutCategory_ReturnsBadRequest() throws Exception {
        CarModel modelWithoutCategory = new CarModel();
        modelWithoutCategory.setId(2L);
        modelWithoutCategory.setBrand("Nissan");
        modelWithoutCategory.setModel("Sentra");
        modelWithoutCategory.setYear(2023);
        modelWithoutCategory.setPricePerDay(new BigDecimal("45.00"));

        when(carModelRepository.findById(2L)).thenReturn(Optional.of(modelWithoutCategory));

        mockMvc.perform(post("/api/admin/cars/models/2/units")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"branchId\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void addUnitsToModel_InvalidBranchId_ReturnsBadRequest() throws Exception {
        when(carModelRepository.findById(1L)).thenReturn(Optional.of(testCarModel));

        mockMvc.perform(post("/api/admin/cars/models/1/units")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"branchId\":\"invalid\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void addUnitsToModel_ValidData_ReturnsSuccess() throws Exception {
        when(carModelRepository.findById(1L)).thenReturn(Optional.of(testCarModel));
        when(branchRepository.findById(1L)).thenReturn(Optional.of(testBranch));
        when(carRepository.save(any(Car.class))).thenAnswer(invocation -> {
            Car car = invocation.getArgument(0);
            car.setId(2L);
            return car;
        });

        mockMvc.perform(post("/api/admin/cars/models/1/units")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"branchId\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void updateModel_ModelNotFound_ReturnsBadRequest() throws Exception {
        CarModelRequest request = new CarModelRequest();
        request.setBrand("Toyota");
        request.setModel("RAV4 Updated");
        request.setYear(2024);
        request.setPricePerDay(new BigDecimal("55.00"));
        request.setCategoryId(1L);
        request.setBranchId(1L);
        request.setQuantity(1);

        when(carModelRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/admin/cars/models/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void updateModel_CategoryNotFound_ReturnsBadRequest() throws Exception {
        CarModelRequest request = new CarModelRequest();
        request.setBrand("Toyota");
        request.setModel("RAV4 Updated");
        request.setYear(2024);
        request.setPricePerDay(new BigDecimal("55.00"));
        request.setCategoryId(999L);
        request.setBranchId(1L);
        request.setQuantity(1);

        when(carModelRepository.findById(1L)).thenReturn(Optional.of(testCarModel));
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/admin/cars/models/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void updateUnit_InvalidStatus_ReturnsBadRequest() throws Exception {
        CarUnitUpdateRequest request = new CarUnitUpdateRequest();
        request.setStatus("UNKNOWN");

        when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));

        mockMvc.perform(put("/api/admin/cars/units/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void deleteUnit_NonExisting_ReturnsBadRequest() throws Exception {
        when(carRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/admin/cars/units/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void updateModel_ExistingModel_ReturnsSuccess() throws Exception {
        CarModelRequest request = new CarModelRequest();
        request.setBrand("Toyota");
        request.setModel("RAV4 Updated");
        request.setYear(2024);
        request.setPricePerDay(new BigDecimal("55.00"));
        request.setCategoryId(1L);
        request.setBranchId(1L);
        request.setQuantity(1);

        when(carModelRepository.findById(1L)).thenReturn(Optional.of(testCarModel));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(carModelRepository.save(any(CarModel.class))).thenReturn(testCarModel);

        mockMvc.perform(put("/api/admin/cars/models/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void deleteModel_ExistingModel_ReturnsSuccess() throws Exception {
        when(carModelRepository.findById(1L)).thenReturn(Optional.of(testCarModel));

        mockMvc.perform(delete("/api/admin/cars/models/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getUnitsByModel_ReturnsUnits() throws Exception {
        when(carRepository.findByCarModelId(1L)).thenReturn(List.of(testCar));

        mockMvc.perform(get("/api/admin/cars/models/1/units"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].plate").value("ABC123"));
    }

    @Test
    void updateUnit_SetAvailableWithPlateAndColor_ReturnsSuccess() throws Exception {
        CarUnitUpdateRequest request = new CarUnitUpdateRequest();
        request.setPlate("XYZ999");
        request.setColor("Azul");
        request.setStatus("AVAILABLE");

        when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));
        when(carRepository.save(any(Car.class))).thenReturn(testCar);

        mockMvc.perform(put("/api/admin/cars/units/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void updateUnit_SetAvailableWithoutPlate_ReturnsBadRequest() throws Exception {
        Car carWithoutPlate = new Car();
        carWithoutPlate.setId(2L);
        carWithoutPlate.setCarModel(testCarModel);
        carWithoutPlate.setStatus(CarStatus.PENDING_REGISTRATION);
        carWithoutPlate.setBranch(testBranch);

        CarUnitUpdateRequest request = new CarUnitUpdateRequest();
        request.setStatus("AVAILABLE");

        when(carRepository.findById(2L)).thenReturn(Optional.of(carWithoutPlate));

        mockMvc.perform(put("/api/admin/cars/units/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void deleteUnit_ExistingUnit_ReturnsSuccess() throws Exception {
        when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));

        mockMvc.perform(delete("/api/admin/cars/units/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
