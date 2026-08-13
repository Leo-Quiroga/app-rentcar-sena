package com.autoreserve.backend.web.controller;

import com.autoreserve.backend.domain.entity.CarModel;
import com.autoreserve.backend.domain.entity.Category;
import com.autoreserve.backend.domain.entity.Favorite;
import com.autoreserve.backend.domain.entity.User;
import com.autoreserve.backend.domain.repository.CarModelRepository;
import com.autoreserve.backend.domain.repository.CarRepository;
import com.autoreserve.backend.domain.repository.FavoriteRepository;
import com.autoreserve.backend.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FavoriteControllerTest {

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private CarRepository carRepository;

    @Mock
    private CarModelRepository carModelRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDetails principal;

    @Test
    void getMyFavoritesReturnsFavoriteSummaries() {
        FavoriteController controller = new FavoriteController(favoriteRepository, carRepository, carModelRepository, userRepository);
        User user = new User();
        user.setId(1L);
        user.setEmail("client@example.com");

        Category category = new Category();
        category.setName("SUV");

        CarModel model = new CarModel();
        model.setId(10L);
        model.setBrand("Toyota");
        model.setModel("RAV4");
        model.setYear(2024);
        model.setPricePerDay(new BigDecimal("100.00"));
        model.setImage("image.jpg");
        model.setDescription("Familiar");
        model.setCategory(category);

        Favorite favorite = new Favorite();
        favorite.setId(5L);
        favorite.setUser(user);
        favorite.setCarModel(model);
        favorite.setCreatedAt(LocalDateTime.now());

        when(principal.getUsername()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(favoriteRepository.findByUserOrderByCreatedAtDesc(user)).thenReturn(List.of(favorite));
        when(carRepository.countAvailableByModel(model.getId())).thenReturn(3L);
        when(carRepository.countByCarModelId(model.getId())).thenReturn(4L);

        ResponseEntity<?> response = controller.getMyFavorites(principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertThat(body.get("success")).isEqualTo(true);
        assertThat(body.get("count")).isEqualTo(1);
    }

    @Test
    void addToFavoritesRejectsMissingCarModelId() {
        FavoriteController controller = new FavoriteController(favoriteRepository, carRepository, carModelRepository, userRepository);
        when(principal.getUsername()).thenReturn("user@example.com");

        ResponseEntity<?> response = controller.addToFavorites(Map.of(), principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(((Map<?, ?>) response.getBody()).get("error")).isEqualTo("carModelId es requerido");
    }

    @Test
    void addToFavoritesRejectsAlreadySavedFavorite() {
        FavoriteController controller = new FavoriteController(favoriteRepository, carRepository, carModelRepository, userRepository);
        User user = new User();
        user.setId(1L);
        user.setEmail("client@example.com");
        CarModel model = new CarModel();
        model.setId(20L);

        when(principal.getUsername()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(carModelRepository.findById(model.getId())).thenReturn(Optional.of(model));
        when(favoriteRepository.existsByUserIdAndCarModelId(user.getId(), model.getId())).thenReturn(true);

        ResponseEntity<?> response = controller.addToFavorites(Map.of("carModelId", model.getId()), principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(((Map<?, ?>) response.getBody()).get("error")).isEqualTo("El modelo ya está en tu lista de favoritos");
    }

    @Test
    void addToFavoritesCreatesFavoriteSuccessfully() {
        FavoriteController controller = new FavoriteController(favoriteRepository, carRepository, carModelRepository, userRepository);
        User user = new User();
        user.setId(2L);
        user.setEmail("cliente@example.com");
        CarModel model = new CarModel();
        model.setId(30L);
        model.setBrand("Honda");
        model.setModel("Civic");
        model.setYear(2023);

        when(principal.getUsername()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(carModelRepository.findById(model.getId())).thenReturn(Optional.of(model));
        when(favoriteRepository.existsByUserIdAndCarModelId(user.getId(), model.getId())).thenReturn(false);
        when(favoriteRepository.save(any(Favorite.class))).thenAnswer(invocation -> {
            Favorite saved = invocation.getArgument(0);
            saved.setId(7L);
            return saved;
        });

        ResponseEntity<?> response = controller.addToFavorites(Map.of("carModelId", model.getId()), principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertThat(body.get("success")).isEqualTo(true);
        assertThat(body.get("favoriteId")).isEqualTo(7L);
    }

    @Test
    void removeFromFavoritesReturnsBadRequestWhenNotPresent() {
        FavoriteController controller = new FavoriteController(favoriteRepository, carRepository, carModelRepository, userRepository);
        User user = new User();
        user.setId(3L);
        user.setEmail("cliente2@example.com");
        CarModel model = new CarModel();
        model.setId(40L);

        when(principal.getUsername()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(carModelRepository.findById(model.getId())).thenReturn(Optional.of(model));
        when(favoriteRepository.findByUserAndCarModel(user, model)).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.removeFromFavorites(model.getId(), principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(((Map<?, ?>) response.getBody()).get("error")).isEqualTo("El modelo no está en tu lista de favoritos");
    }

    @Test
    void removeFromFavoritesDeletesAndReturnsSuccess() {
        FavoriteController controller = new FavoriteController(favoriteRepository, carRepository, carModelRepository, userRepository);
        User user = new User();
        user.setId(4L);
        user.setEmail("cliente3@example.com");
        CarModel model = new CarModel();
        model.setId(50L);
        model.setBrand("Ford");
        model.setModel("Focus");

        Favorite favorite = new Favorite();
        favorite.setId(8L);
        favorite.setUser(user);
        favorite.setCarModel(model);

        when(principal.getUsername()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(carModelRepository.findById(model.getId())).thenReturn(Optional.of(model));
        when(favoriteRepository.findByUserAndCarModel(user, model)).thenReturn(Optional.of(favorite));

        ResponseEntity<?> response = controller.removeFromFavorites(model.getId(), principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((Map<?, ?>) response.getBody()).get("success")).isEqualTo(true);
    }

    @Test
    void isFavoriteAndGetFavoriteIdsReturnExpectedValues() {
        FavoriteController controller = new FavoriteController(favoriteRepository, carRepository, carModelRepository, userRepository);
        User user = new User();
        user.setId(5L);
        user.setEmail("cliente4@example.com");

        when(principal.getUsername()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(favoriteRepository.existsByUserIdAndCarModelId(user.getId(), 60L)).thenReturn(true);
        when(favoriteRepository.findFavoriteCarModelIdsByUserId(user.getId())).thenReturn(Set.of(60L, 70L));

        ResponseEntity<?> favoriteResponse = controller.isFavorite(60L, principal);
        assertThat(favoriteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((Map<?, ?>) favoriteResponse.getBody()).get("isFavorite")).isEqualTo(true);

        ResponseEntity<?> idsResponse = controller.getFavoriteIds(principal);
        assertThat(idsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((Map<?, ?>) idsResponse.getBody()).get("count")).isEqualTo(2);
    }

    @Test
    void isFavorite_ReturnsFalseWhenNotFavorite() {
        FavoriteController controller = new FavoriteController(favoriteRepository, carRepository, carModelRepository, userRepository);
        User user = new User();
        user.setId(6L);
        user.setEmail("cliente5@example.com");

        when(principal.getUsername()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(favoriteRepository.existsByUserIdAndCarModelId(user.getId(), 80L)).thenReturn(false);

        ResponseEntity<?> response = controller.isFavorite(80L, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((Map<?, ?>) response.getBody()).get("isFavorite")).isEqualTo(false);
    }

    @Test
    void getFavoriteIds_ReturnsEmptySetWhenNoFavorites() {
        FavoriteController controller = new FavoriteController(favoriteRepository, carRepository, carModelRepository, userRepository);
        User user = new User();
        user.setId(7L);
        user.setEmail("cliente6@example.com");

        when(principal.getUsername()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(favoriteRepository.findFavoriteCarModelIdsByUserId(user.getId())).thenReturn(Set.of());

        ResponseEntity<?> response = controller.getFavoriteIds(principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((Map<?, ?>) response.getBody()).get("count")).isEqualTo(0);
    }

    @Test
    void getFavoriteIds_UserNotFound_ReturnsBadRequest() {
        FavoriteController controller = new FavoriteController(favoriteRepository, carRepository, carModelRepository, userRepository);

        when(principal.getUsername()).thenReturn("unknown@example.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.getFavoriteIds(principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(((Map<?, ?>) response.getBody()).get("error")).isEqualTo("Usuario no encontrado");
    }

    @Test
    void isFavorite_UserNotFound_ReturnsBadRequest() {
        FavoriteController controller = new FavoriteController(favoriteRepository, carRepository, carModelRepository, userRepository);

        when(principal.getUsername()).thenReturn("unknown@example.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.isFavorite(80L, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(((Map<?, ?>) response.getBody()).get("error")).isEqualTo("Usuario no encontrado");
    }

    @Test
    void removeFromFavorites_UserNotFound_ReturnsBadRequest() {
        FavoriteController controller = new FavoriteController(favoriteRepository, carRepository, carModelRepository, userRepository);

        when(principal.getUsername()).thenReturn("unknown@example.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.removeFromFavorites(90L, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(((Map<?, ?>) response.getBody()).get("error")).isEqualTo("Usuario no encontrado");
    }

    @Test
    void addToFavorites_Unauthenticated_ReturnsBadRequest() {
        FavoriteController controller = new FavoriteController(favoriteRepository, carRepository, carModelRepository, userRepository);

        ResponseEntity<?> response = controller.addToFavorites(Map.of("carModelId", 1L), null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(((Map<?, ?>) response.getBody()).get("error")).isEqualTo("Usuario no autenticado");
    }

    @Test
    void addToFavorites_DbConstraintViolationReturnsBadRequest() {
        FavoriteController controller = new FavoriteController(favoriteRepository, carRepository, carModelRepository, userRepository);
        User user = new User();
        user.setId(8L);
        user.setEmail("cliente7@example.com");
        CarModel model = new CarModel();
        model.setId(90L);
        model.setBrand("Nissan");
        model.setModel("Leaf");

        when(principal.getUsername()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(carModelRepository.findById(model.getId())).thenReturn(Optional.of(model));
        when(favoriteRepository.existsByUserIdAndCarModelId(user.getId(), model.getId())).thenReturn(false);
        when(favoriteRepository.save(any(Favorite.class))).thenThrow(new RuntimeException("Duplicate entry"));

        ResponseEntity<?> response = controller.addToFavorites(Map.of("carModelId", model.getId()), principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(((Map<?, ?>) response.getBody()).get("error")).isEqualTo("El modelo ya está en tu lista de favoritos");
    }

    @Test
    void addToFavorites_ConstraintViolationReturnsBadRequest() {
        FavoriteController controller = new FavoriteController(favoriteRepository, carRepository, carModelRepository, userRepository);
        User user = new User();
        user.setId(11L);
        user.setEmail("cliente10@example.com");
        CarModel model = new CarModel();
        model.setId(93L);
        model.setBrand("Hyundai");
        model.setModel("Tucson");

        when(principal.getUsername()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(carModelRepository.findById(model.getId())).thenReturn(Optional.of(model));
        when(favoriteRepository.existsByUserIdAndCarModelId(user.getId(), model.getId())).thenReturn(false);
        when(favoriteRepository.save(any(Favorite.class))).thenThrow(new RuntimeException("Unique index violated"));

        ResponseEntity<?> response = controller.addToFavorites(Map.of("carModelId", model.getId()), principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(((Map<?, ?>) response.getBody()).get("error")).isEqualTo("El modelo ya está en tu lista de favoritos");
    }

    @Test
    void addToFavorites_RuntimeExceptionReturnsRawMessage() {
        FavoriteController controller = new FavoriteController(favoriteRepository, carRepository, carModelRepository, userRepository);
        User user = new User();
        user.setId(9L);
        user.setEmail("cliente8@example.com");
        CarModel model = new CarModel();
        model.setId(91L);
        model.setBrand("Mazda");
        model.setModel("CX-5");

        when(principal.getUsername()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(carModelRepository.findById(model.getId())).thenReturn(Optional.of(model));
        when(favoriteRepository.existsByUserIdAndCarModelId(user.getId(), model.getId())).thenReturn(false);
        when(favoriteRepository.save(any(Favorite.class))).thenThrow(new RuntimeException("Unexpected database error"));

        ResponseEntity<?> response = controller.addToFavorites(Map.of("carModelId", model.getId()), principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(((Map<?, ?>) response.getBody()).get("error")).isEqualTo("Unexpected database error");
    }

    @Test
    void getMyFavorites_UserNotFound_ReturnsBadRequest() {
        FavoriteController controller = new FavoriteController(favoriteRepository, carRepository, carModelRepository, userRepository);
        when(principal.getUsername()).thenReturn("unknown@example.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.getMyFavorites(principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(((Map<?, ?>) response.getBody()).get("error")).isEqualTo("Usuario no encontrado");
    }

    @Test
    void addToFavorites_UserNotFound_ReturnsBadRequest() {
        FavoriteController controller = new FavoriteController(favoriteRepository, carRepository, carModelRepository, userRepository);
        when(principal.getUsername()).thenReturn("unknown@example.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.addToFavorites(Map.of("carModelId", 1L), principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(((Map<?, ?>) response.getBody()).get("error")).isEqualTo("Usuario no encontrado");
    }

    @Test
    void addToFavorites_CarModelNotFound_ReturnsBadRequest() {
        FavoriteController controller = new FavoriteController(favoriteRepository, carRepository, carModelRepository, userRepository);
        User user = new User();
        user.setId(10L);
        user.setEmail("client@example.com");

        when(principal.getUsername()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(carModelRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.addToFavorites(Map.of("carModelId", 1L), principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(((Map<?, ?>) response.getBody()).get("error")).isEqualTo("Modelo no encontrado con ID: 1");
    }

    @Test
    void removeFromFavorites_CarModelNotFound_ReturnsBadRequest() {
        FavoriteController controller = new FavoriteController(favoriteRepository, carRepository, carModelRepository, userRepository);
        User user = new User();
        user.setId(11L);
        user.setEmail("client2@example.com");

        when(principal.getUsername()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(carModelRepository.findById(2L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.removeFromFavorites(2L, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(((Map<?, ?>) response.getBody()).get("error")).isEqualTo("Modelo no encontrado con ID: 2");
    }
}
