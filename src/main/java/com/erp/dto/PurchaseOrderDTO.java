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
public class PurchaseOrderDTO {
    private Long id;
    private String poNumber;

    // Supplier info
    private Long supplierId;
    private String supplierName;
    private String supplierPhone;

    // Branch & Warehouse
    private Long branchId;
    private String branchName;
    private Long warehouseId;
    private String warehouseName;

    // Status
    private Long statusId;
    private String statusCode;
    private String statusNameAr;
    private String statusNameEn;

    // Dates
    private LocalDateTime orderDate;
    private LocalDate expectedDate;
    private LocalDateTime receivedDate;

    // Financials
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;
    private BigDecimal shippingCost;
    private BigDecimal totalAmount;

    // Approval
    private Long requestedById;
    private String requestedByName;
    private Long approvedById;
    private String approvedByName;
    private LocalDateTime approvedAt;

    // Payment
    private String paymentTerms;
    private String paymentStatus;

    // Items
    private List<PurchaseOrderItemDTO> items;

    // Audit
    private String notes;
    private Long createdById;
    private String createdByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}