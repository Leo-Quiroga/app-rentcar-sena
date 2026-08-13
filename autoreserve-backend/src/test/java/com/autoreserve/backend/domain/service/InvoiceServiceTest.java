package com.autoreserve.backend.domain.service;

import com.autoreserve.backend.domain.entity.Invoice;
import com.autoreserve.backend.domain.repository.InvoiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @InjectMocks
    private InvoiceService invoiceService;

    private Invoice testInvoice;

    @BeforeEach
    void setUp() {
        testInvoice = new Invoice();
        testInvoice.setInvoiceNumber("INV-001");
        testInvoice.setTotalAmount(BigDecimal.valueOf(100.00));
    }

    @Test
    void save_ReturnsSavedInvoice() {
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(testInvoice);

        Invoice result = invoiceService.save(testInvoice);

        assertNotNull(result);
        assertEquals("INV-001", result.getInvoiceNumber());
        verify(invoiceRepository, times(1)).save(testInvoice);
    }

    @Test
    void findById_ExistingInvoice_ReturnsOptional() {
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber("INV-001");
        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));

        Optional<Invoice> result = invoiceService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("INV-001", result.get().getInvoiceNumber());
        verify(invoiceRepository, times(1)).findById(1L);
    }

    @Test
    void findById_NonExistingInvoice_ReturnsEmpty() {
        when(invoiceRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Invoice> result = invoiceService.findById(999L);

        assertFalse(result.isPresent());
        verify(invoiceRepository, times(1)).findById(999L);
    }
}
