package com.autoreserve.backend.domain.service;

import com.autoreserve.backend.domain.entity.Invoice;
import com.autoreserve.backend.domain.repository.InvoiceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;

    public InvoiceService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    public Invoice save(Invoice invoice) {
        return invoiceRepository.save(invoice);
    }

    public Optional<Invoice> findById(Long id) {
        return invoiceRepository.findById(id);
    }
}
