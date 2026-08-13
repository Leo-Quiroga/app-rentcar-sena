package com.autoreserve.backend.domain.service;

import com.autoreserve.backend.domain.entity.CarModel;
import com.autoreserve.backend.domain.entity.Favorite;
import com.autoreserve.backend.domain.entity.User;
import com.autoreserve.backend.domain.repository.FavoriteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

    @Mock
    private FavoriteRepository favoriteRepository;

    @InjectMocks
    private FavoriteService favoriteService;

    private User testUser;
    private CarModel testCarModel;
    private Favorite testFavorite;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");

        testCarModel = new CarModel();
        testCarModel.setId(1L);
        testCarModel.setBrand("Toyota");

        testFavorite = new Favorite();
        testFavorite.setId(1L);
        testFavorite.setUser(testUser);
        testFavorite.setCarModel(testCarModel);
    }

    @Test
    void addToFavorites_NewFavorite_ReturnsSaved() {
        when(favoriteRepository.findByUserAndCarModel(testUser, testCarModel)).thenReturn(Optional.empty());
        when(favoriteRepository.save(any(Favorite.class))).thenReturn(testFavorite);

        Favorite result = favoriteService.addToFavorites(testUser, testCarModel);

        assertThat(result.getId()).isEqualTo(1L);
        verify(favoriteRepository).save(any(Favorite.class));
    }

    @Test
    void addToFavorites_AlreadyExists_ThrowsException() {
        when(favoriteRepository.findByUserAndCarModel(testUser, testCarModel)).thenReturn(Optional.of(testFavorite));

        assertThatThrownBy(() -> favoriteService.addToFavorites(testUser, testCarModel))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("ya está en favoritos");
    }

    @Test
    void removeFromFavorites_ExistingFavorite_DeletesIt() {
        when(favoriteRepository.findByUserAndCarModel(testUser, testCarModel)).thenReturn(Optional.of(testFavorite));

        favoriteService.removeFromFavorites(testUser, testCarModel);

        verify(favoriteRepository).delete(testFavorite);
    }

    @Test
    void removeFromFavorites_NonExisting_ThrowsException() {
        when(favoriteRepository.findByUserAndCarModel(testUser, testCarModel)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> favoriteService.removeFromFavorites(testUser, testCarModel))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("no está en favoritos");
    }

    @Test
    void isFavorite_WhenExists_ReturnsTrue() {
        when(favoriteRepository.findByUserAndCarModel(testUser, testCarModel)).thenReturn(Optional.of(testFavorite));

        assertThat(favoriteService.isFavorite(testUser, testCarModel)).isTrue();
    }

    @Test
    void isFavorite_WhenNotExists_ReturnsFalse() {
        when(favoriteRepository.findByUserAndCarModel(testUser, testCarModel)).thenReturn(Optional.empty());

        assertThat(favoriteService.isFavorite(testUser, testCarModel)).isFalse();
    }

    @Test
    void getUserFavorites_ReturnsOrderedList() {
        when(favoriteRepository.findByUserOrderByCreatedAtDesc(testUser)).thenReturn(List.of(testFavorite));

        List<Favorite> result = favoriteService.getUserFavorites(testUser);

        assertThat(result).hasSize(1);
    }

    @Test
    void save_ReturnsSavedFavorite() {
        when(favoriteRepository.save(any(Favorite.class))).thenReturn(testFavorite);

        Favorite result = favoriteService.save(testFavorite);

        assertThat(result.getId()).isEqualTo(1L);
        verify(favoriteRepository).save(testFavorite);
    }

    @Test
    void deleteById_CallsRepository() {
        doNothing().when(favoriteRepository).deleteById(1L);

        favoriteService.deleteById(1L);

        verify(favoriteRepository).deleteById(1L);
    }

    @Test
    void getFavoriteModelIds_ReturnsSetOfIds() {
        when(favoriteRepository.findFavoriteCarModelIdsByUserId(1L)).thenReturn(Set.of(1L, 2L));

        Set<Long> result = favoriteService.getFavoriteModelIds(1L);

        assertThat(result).hasSize(2);
        assertThat(result).contains(1L, 2L);
        verify(favoriteRepository).findFavoriteCarModelIdsByUserId(1L);
    }

    @Test
    void getFavoriteStatistics_ReturnsStatistics() {
        List<Object[]> stats = new java.util.ArrayList<>();
        stats.add(new Object[]{1L, 10L});
        when(favoriteRepository.countFavoritesByCarModel()).thenReturn(stats);

        List<Object[]> result = favoriteService.getFavoriteStatistics();

        assertThat(result).hasSize(1);
        verify(favoriteRepository).countFavoritesByCarModel();
    }

    @Test
    void getUsersWithFavoriteModel_ReturnsUsers() {
        when(favoriteRepository.findUsersByFavoriteCarModel(1L)).thenReturn(List.of(testUser));

        List<User> result = favoriteService.getUsersWithFavoriteModel(1L);

        assertThat(result).hasSize(1);
        verify(favoriteRepository).findUsersByFavoriteCarModel(1L);
    }
}
