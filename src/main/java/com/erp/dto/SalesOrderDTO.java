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
public class SalesOrderDTO {
    private Long id;
    private String orderNumber;

    // Customer info
    private Long customerId;
    private String customerName;
    private String customerPhone;

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
    private LocalDate requiredDate;
    private LocalDateTime shippingDate;

    // Financials
    private BigDecimal subtotal;
    private String discountType;
    private BigDecimal discountValue;
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
    private String rejectionReason;

    // Shipping
    private String shippingAddress;
    private String trackingNumber;

    // Items
    private List<SalesOrderItemDTO> items;

    // Audit
    private String notes;
    private Long createdById;
    private String createdByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}