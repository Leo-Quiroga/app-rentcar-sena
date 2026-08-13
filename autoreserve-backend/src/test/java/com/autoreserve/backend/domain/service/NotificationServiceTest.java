package com.autoreserve.backend.domain.service;

import com.autoreserve.backend.domain.entity.Notification;
import com.autoreserve.backend.domain.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void save_ReturnsPersistedNotification() {
        Notification notification = new Notification();
        notification.setMessage("Test notification");
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        Notification result = notificationService.save(notification);

        assertThat(result.getMessage()).isEqualTo("Test notification");
        verify(notificationRepository).save(notification);
    }

    @Test
    void findAll_ReturnsAllNotifications() {
        Notification n1 = new Notification();
        Notification n2 = new Notification();
        when(notificationRepository.findAll()).thenReturn(List.of(n1, n2));

        List<Notification> result = notificationService.findAll();

        assertThat(result).hasSize(2);
    }

    @Test
    void findById_ExistingNotification_ReturnsOptional() {
        Notification notification = new Notification();
        notification.setMessage("Test notification");
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        Optional<Notification> result = notificationService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Test notification", result.get().getMessage());
        verify(notificationRepository).findById(1L);
    }

    @Test
    void findById_NonExistingNotification_ReturnsEmpty() {
        when(notificationRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Notification> result = notificationService.findById(999L);

        assertFalse(result.isPresent());
        verify(notificationRepository).findById(999L);
    }
}
