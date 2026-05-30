package com.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDTO {
    private Long id;
    private String invoiceNumber;

    // Sales Order info
    private Long salesOrderId;
    private String salesOrderNumber;

    // Customer info
    private Long customerId;
    private String customerName;
    private String customerPhone;
    private String customerEmail;

    // Branch info
    private Long branchId;
    private String branchName;

    // Status
    private Long statusId;
    private String statusCode;
    private String statusNameAr;
    private String statusNameEn;

    // Dates
    private LocalDateTime invoiceDate;
    private LocalDate dueDate;

    // Financials
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private BigDecimal amountPaid;
    private BigDecimal balanceDue;

    // Payment
    private String paymentMethod;
    private String paymentStatus;

    // Items
    private List<InvoiceItemDTO> items;

    // Audit
    private String notes;
    private Long createdById;
    private String createdByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}