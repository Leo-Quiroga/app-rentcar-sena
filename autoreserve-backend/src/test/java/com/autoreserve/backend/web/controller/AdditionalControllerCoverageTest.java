package com.autoreserve.backend.web.controller;

import com.autoreserve.backend.domain.entity.Branch;
import com.autoreserve.backend.domain.entity.Car;
import com.autoreserve.backend.domain.entity.CarModel;
import com.autoreserve.backend.domain.entity.Category;
import com.autoreserve.backend.domain.entity.Role;
import com.autoreserve.backend.domain.entity.User;
import com.autoreserve.backend.domain.repository.BranchRepository;
import com.autoreserve.backend.domain.repository.CarModelRepository;
import com.autoreserve.backend.domain.repository.CarRepository;
import com.autoreserve.backend.domain.repository.CategoryRepository;
import com.autoreserve.backend.domain.repository.RoleRepository;
import com.autoreserve.backend.domain.repository.UserRepository;
import com.autoreserve.backend.dto.auth.LoginRequest;
import com.autoreserve.backend.dto.auth.LoginResponse;
import com.autoreserve.backend.dto.auth.RegisterRequest;
import com.autoreserve.backend.dto.branch.BranchResponse;
import com.autoreserve.backend.dto.branch.BranchRequest;
import com.autoreserve.backend.dto.car.CarModelRequest;
import com.autoreserve.backend.dto.car.CarModelResponse;
import com.autoreserve.backend.dto.car.CarUnitUpdateRequest;
import com.autoreserve.backend.dto.category.CategoryRequest;
import com.autoreserve.backend.dto.category.CategoryResponse;
import com.autoreserve.backend.security.jwt.JwtService;
import com.autoreserve.backend.web.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdditionalControllerCoverageTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private BranchRepository branchRepository;
    @Mock
    private CarModelRepository carModelRepository;
    @Mock
    private CarRepository carRepository;

    @Test
    void authControllerLoginReturnsUnprocessableForInvalidEmail() {
        AuthController controller = new AuthController(authenticationManager, userRepository, roleRepository, jwtService, passwordEncoder);
        LoginRequest request = new LoginRequest();
        request.setEmail("user-at-example.com");
        request.setPassword("secret");

        ResponseEntity<?> response = controller.login(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat((String) ((Map<?, ?>) response.getBody()).get("error")).isEqualTo("El formato del correo no es válido");
    }

    @Test
    void authControllerLoginReturnsNotFoundForUnknownUser() {
        AuthController controller = new AuthController(authenticationManager, userRepository, roleRepository, jwtService, passwordEncoder);
        LoginRequest request = new LoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("secret");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(java.util.Optional.empty());

        ResponseEntity<?> response = controller.login(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat((String) ((Map<?, ?>) response.getBody()).get("error")).isEqualTo("Usuario no encontrado");
    }

    @Test
    void authControllerLoginSuccessReturnsToken() {
        AuthController controller = new AuthController(authenticationManager, userRepository, roleRepository, jwtService, passwordEncoder);
        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        Role role = new Role();
        role.setName("CLIENT");
        user.setRole(role);

        LoginRequest request = new LoginRequest();
        request.setEmail(user.getEmail());
        request.setPassword("password");

        Authentication auth = mock(Authentication.class);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(java.util.Optional.of(user));
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(jwtService.generateToken(user.getEmail(), role.getName())).thenReturn("jwt-token");

        ResponseEntity<?> response = controller.login(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(body).containsEntry("success", true);
        assertThat(((LoginResponse) body.get("data")).getEmail()).isEqualTo(user.getEmail());
        assertThat(((LoginResponse) body.get("data")).getToken()).isEqualTo("jwt-token");
    }

    @Test
    void authControllerRegisterCreatesUserAndReturnsCreated() {
        AuthController controller = new AuthController(authenticationManager, userRepository, roleRepository, jwtService, passwordEncoder);
        RegisterRequest request = new RegisterRequest();
        request.setFirstName("Juan");
        request.setLastName("Perez");
        request.setEmail("juan@example.com");
        request.setPassword("password");
        request.setPhone("123456789");

        Role clientRole = new Role();
        clientRole.setName("CLIENT");
        when(userRepository.findByEmail(request.getEmail())).thenReturn(java.util.Optional.empty());
        when(roleRepository.findByName("CLIENT")).thenReturn(java.util.Optional.of(clientRole));
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded");
        when(jwtService.generateToken(request.getEmail(), clientRole.getName())).thenReturn("new-token");

        ResponseEntity<?> response = controller.register(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(body).containsEntry("success", true);
        assertThat(((LoginResponse) body.get("data")).getToken()).isEqualTo("new-token");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void categoryControllerReturnsCategoriesAndSingleCategory() {
        CategoryController controller = new CategoryController(categoryRepository);
        Category category = new Category();
        category.setId(5L);
        category.setName("Sedán");

        when(categoryRepository.findAll()).thenReturn(List.of(category));
        when(categoryRepository.findById(5L)).thenReturn(java.util.Optional.of(category));

        ResponseEntity<List<CategoryResponse>> allResponse = controller.getAllCategories();
        assertThat(allResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(allResponse.getBody()).hasSize(1);
        assertThat(allResponse.getBody().get(0).getName()).isEqualTo("Sedán");

        ResponseEntity<CategoryResponse> singleResponse = controller.getCategoryById(5L);
        assertThat(singleResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(singleResponse.getBody().getId()).isEqualTo(5L);
    }

    @Test
    void branchControllerReturnsBranchesAndById() {
        BranchController controller = new BranchController(branchRepository);
        Branch branch = new Branch();
        branch.setId(7L);
        branch.setName("Sede Central");

        when(branchRepository.findAll()).thenReturn(List.of(branch));
        when(branchRepository.findById(7L)).thenReturn(java.util.Optional.of(branch));

        ResponseEntity<List<BranchResponse>> allResponse = controller.getAllBranches();
        assertThat(allResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(allResponse.getBody()).hasSize(1);
        assertThat(allResponse.getBody().get(0).getName()).isEqualTo("Sede Central");

        ResponseEntity<BranchResponse> byIdResponse = controller.getBranchById(7L);
        assertThat(byIdResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(byIdResponse.getBody().getId()).isEqualTo(7L);
    }

    @Test
    void carControllerReturnsAvailableModelsAndModelById() {
        CarController controller = new CarController(carModelRepository, carRepository);
        Category category = new Category();
        category.setName("SUV");
        category.setId(2L);
        CarModel model = new CarModel();
        model.setId(10L);
        model.setBrand("Toyota");
        model.setModel("RAV4");
        model.setYear(2025);
        model.setPricePerDay(new BigDecimal("120.00"));
        model.setImage("img.jpg");
        model.setDescription("Todo terreno");
        model.setCategory(category);

        when(carModelRepository.findModelsWithAvailableUnits(null)).thenReturn(List.of(model));
        when(carRepository.countAvailableByModel(10L)).thenReturn(3L);
        when(carRepository.countByCarModelId(10L)).thenReturn(5L);

        ResponseEntity<List<CarModelResponse>> availableResponse = controller.getAvailableModels(null);
        assertThat(availableResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(availableResponse.getBody()).hasSize(1);
        assertThat(availableResponse.getBody().get(0).getAvailableUnits()).isEqualTo(3L);

        when(carModelRepository.findById(10L)).thenReturn(java.util.Optional.of(model));
        ResponseEntity<?> modelById = controller.getModelById(10L);
        assertThat(modelById.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void searchControllerValidatesDatesAndReturnsModels() {
        SearchController controller = new SearchController(carModelRepository, carRepository);
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        ResponseEntity<?> invalidResponse = controller.searchAvailableModels(today, yesterday, null);
        assertThat(invalidResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        Category category = new Category();
        category.setId(1L);
        category.setName("Hatchback");
        CarModel model = new CarModel();
        model.setId(20L);
        model.setBrand("Nissan");
        model.setModel("Versa");
        model.setYear(2024);
        model.setPricePerDay(new BigDecimal("85.00"));
        model.setImage("img.png");
        model.setDescription("Compacto");
        model.setCategory(category);

        when(carModelRepository.findAvailableModels(today, today.plusDays(1), null)).thenReturn(List.of(model));
        when(carRepository.findAvailableUnitForModel(20L, today, today.plusDays(1))).thenReturn(List.of(new Car()));
        when(carRepository.countByCarModelId(20L)).thenReturn(2L);

        ResponseEntity<?> validResponse = controller.searchAvailableModels(today, today.plusDays(1), null);
        assertThat(validResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((List<?>) validResponse.getBody())).hasSize(1);
    }

    @Test
    void adminCategoryControllerCreateAndDeleteBehavior() {
        AdminCategoryController controller = new AdminCategoryController(categoryRepository);

        CategoryRequest createRequest = new CategoryRequest();
        createRequest.setName("Deportivo");
        createRequest.setDescription("Rápido");
        createRequest.setImage("image.jpg");

        Category savedCategory = new Category();
        savedCategory.setId(99L);
        savedCategory.setName(createRequest.getName());
        savedCategory.setDescription(createRequest.getDescription());
        savedCategory.setImage(createRequest.getImage());

        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);
        ResponseEntity<?> createResponse = controller.createCategory(createRequest);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat((String) ((Map<?, ?>) createResponse.getBody()).get("message")).isEqualTo("Categoría creada exitosamente");

        Category categoryWithModels = new Category();
        categoryWithModels.setId(100L);
        categoryWithModels.setCarModels(List.of(new CarModel()));
        when(categoryRepository.findById(100L)).thenReturn(java.util.Optional.of(categoryWithModels));

        ResponseEntity<?> deleteResponse = controller.deleteCategory(100L);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void adminCarControllerHandlesModelsAndUnitValidation() {
        AdminCarController controller = new AdminCarController(carRepository, carModelRepository, categoryRepository, branchRepository);

        Category category = new Category();
        category.setId(5L);
        category.setName("Lujo");
        CarModel model = new CarModel();
        model.setId(101L);
        model.setBrand("BMW");
        model.setModel("X5");
        model.setYear(2026);
        model.setPricePerDay(new BigDecimal("250.00"));
        model.setImage("bmw.jpg");
        model.setDescription("SUV premium");
        model.setCategory(category);

        Branch branch = new Branch();
        branch.setId(55L);
        branch.setName("Sucursal Premium");

        Car car = new Car();
        car.setId(201L);
        car.setBranch(branch);
        car.setCarModel(model);
        car.setPlate("XYZ-100");

        when(carModelRepository.findAll()).thenReturn(List.of(model));
        when(carRepository.countAvailableByModel(101L)).thenReturn(4L);
        when(carRepository.countByCarModelId(101L)).thenReturn(6L);
        when(carRepository.findByCarModelId(101L)).thenReturn(List.of(car));

        ResponseEntity<?> allModelsResponse = controller.getAllCars();
        assertThat(allModelsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((List<?>) allModelsResponse.getBody())).hasSize(1);

        CarModelRequest request = new CarModelRequest();
        request.setBrand("Mercedes");
        request.setModel("GLA");
        request.setYear(2025);
        request.setPricePerDay(new BigDecimal("180.00"));
        request.setImage("mercedes.jpg");
        request.setDescription("Compact luxury");
        request.setCategoryId(category.getId());
        request.setBranchId(branch.getId());
        request.setQuantity(2);

        when(categoryRepository.findById(category.getId())).thenReturn(java.util.Optional.of(category));
        when(branchRepository.findById(branch.getId())).thenReturn(java.util.Optional.of(branch));
        when(carModelRepository.save(any(CarModel.class))).thenAnswer(invocation -> {
            CarModel saved = invocation.getArgument(0);
            saved.setId(202L);
            return saved;
        });
        when(carRepository.save(any(Car.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<?> createModelResponse = controller.createModel(request);
        assertThat(createModelResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((Map<?, ?>) createModelResponse.getBody()).get("unitsCreated")).isEqualTo(2);

        Car unit = new Car();
        unit.setId(202L);
        unit.setCarModel(model);
        unit.setStatus(null);
        when(carRepository.findById(202L)).thenReturn(java.util.Optional.of(unit));

        CarUnitUpdateRequest updateRequest = new CarUnitUpdateRequest();
        updateRequest.setStatus("AVAILABLE");
        ResponseEntity<?> updateResponse = controller.updateUnit(202L, updateRequest);
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void globalExceptionHandlerHandlesValidationAndRuntime() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        MethodArgumentNotValidException invalidException = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        org.springframework.validation.FieldError error = new org.springframework.validation.FieldError("object", "email", "Email inválido");
        when(invalidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(error));

        ResponseEntity<Map<String, String>> validationResponse = handler.handleValidationErrors(invalidException);
        assertThat(validationResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(validationResponse.getBody()).containsEntry("email", "Email inválido");

        ResponseEntity<String> runtimeResponse = handler.handleRuntime(new RuntimeException("Oh no"));
        assertThat(runtimeResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(runtimeResponse.getBody()).isEqualTo("Oh no");
    }
}
