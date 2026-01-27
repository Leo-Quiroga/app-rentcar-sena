package com.autoreserve.backend.domain.repository;

import com.autoreserve.backend.domain.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
}
