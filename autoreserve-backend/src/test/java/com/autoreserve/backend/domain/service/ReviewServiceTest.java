package com.autoreserve.backend.domain.service;

import com.autoreserve.backend.domain.entity.Review;
import com.autoreserve.backend.domain.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewService reviewService;

    private Review testReview;

    @BeforeEach
    void setUp() {
        testReview = new Review();
        testReview.setRating(5);
        testReview.setComment("Excelente servicio");
    }

    @Test
    void save_ReturnsSavedReview() {
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        Review result = reviewService.save(testReview);

        assertNotNull(result);
        assertEquals(5, result.getRating());
        assertEquals("Excelente servicio", result.getComment());
        verify(reviewRepository, times(1)).save(testReview);
    }

    @Test
    void findAll_ReturnsAllReviews() {
        when(reviewRepository.findAll()).thenReturn(List.of(testReview));

        List<Review> result = reviewService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(5, result.get(0).getRating());
        verify(reviewRepository, times(1)).findAll();
    }
}
