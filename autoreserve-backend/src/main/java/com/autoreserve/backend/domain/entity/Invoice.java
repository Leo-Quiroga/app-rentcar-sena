package com.autoreserve.backend.domain.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa el documento fiscal o factura generada por un pago.
 * Mapea la tabla "invoice" y registra los detalles contables de la transacción.
 */
@Entity
@Table(name = "invoice")
public class Invoice {

    /**
     * Identificador único de la factura en la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Relación uno a uno con la entidad Payment.
     * Cada factura está vinculada de forma obligatoria y única a un registro de pago.
     */
    @OneToOne
    @JoinColumn(name = "payment_id", nullable = false, unique = true)
    private Payment payment;

    /**
     * Número de comprobante fiscal único.
     * Identificador alfanumérico utilizado para fines de auditoría y seguimiento legal.
     */
    @Column(name = "invoice_number", nullable = false, unique = true, length = 50)
    private String invoiceNumber;

    /**
     * Fecha y hora de emisión del documento.
     */
    @Column(name = "issue_date")
    private LocalDateTime issueDate;

    /**
     * Monto total facturado.
     * Se define con precisión de 10 dígitos y 2 decimales para consistencia financiera.
     */
    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    /**
     * Información adicional o desglose de los conceptos facturados.
     * Definido como TEXT para soportar descripciones extensas.
     */
    @Column(columnDefinition = "TEXT")
    private String details;

    /**
     * Constructor por defecto requerido por JPA.
     */
    public Invoice() {
    }

    /* ================= GETTERS & SETTERS ================= */

    public Long getId() {
        return id;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public LocalDateTime getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDateTime issueDate) {
        this.issueDate = issueDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}