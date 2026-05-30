package com.erp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoices")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_number", nullable = false, unique = true, length = 50)
    private String invoiceNumber;

    @ManyToOne
    @JoinColumn(name = "sales_order_id")
    private SalesOrder salesOrder;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private DocumentStatus status;

    // Invoice Details
    @Column(name = "invoice_date")
    private LocalDateTime invoiceDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    // Financials
    private BigDecimal subtotal;

    @Column(name = "discount_amount")
    private BigDecimal discountAmount;

    @Column(name = "tax_rate")
    private BigDecimal taxRate;

    @Column(name = "tax_amount")
    private BigDecimal taxAmount;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "amount_paid")
    private BigDecimal amountPaid;

    @Column(name = "balance_due")
    private BigDecimal balanceDue;

    // Payment
    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(name = "payment_status", length = 20)
    private String paymentStatus;

    // Audit
    @Column(length = 500)
    private String notes;

    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<InvoiceItem> items = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        isDeleted = false;
        if (subtotal == null) subtotal = BigDecimal.ZERO;
        if (discountAmount == null) discountAmount = BigDecimal.ZERO;
        if (taxAmount == null) taxAmount = BigDecimal.ZERO;
        if (totalAmount == null) totalAmount = BigDecimal.ZERO;
        if (amountPaid == null) amountPaid = BigDecimal.ZERO;
        if (balanceDue == null) balanceDue = BigDecimal.ZERO;
        if (paymentStatus == null) paymentStatus = "UNPAID";
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        // Recalculate balance due
        if (totalAmount != null && amountPaid != null) {
            balanceDue = totalAmount.subtract(amountPaid);
            if (balanceDue.compareTo(BigDecimal.ZERO) <= 0) {
                paymentStatus = "PAID";
            } else if (amountPaid.compareTo(BigDecimal.ZERO) > 0) {
                paymentStatus = "PARTIAL";
            }
        }
    }
}