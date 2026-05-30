package com.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferOrderDTO {
    private Long id;
    private String transferNumber;

    // Branch info
    private Long fromBranchId;
    private String fromBranchName;
    private Long toBranchId;
    private String toBranchName;

    // Warehouse info
    private Long fromWarehouseId;
    private String fromWarehouseName;
    private Long toWarehouseId;
    private String toWarehouseName;

    // Status
    private Long statusId;
    private String statusCode;
    private String statusNameAr;
    private String statusNameEn;

    // Dates
    private LocalDateTime transferDate;
    private LocalDateTime expectedDate;
    private LocalDateTime receivedDate;

    // Transfer Details
    private String transferReason;
    private String shippingMethod;
    private String trackingNumber;
    private BigDecimal totalValue;

    // Approval
    private Long requestedById;
    private String requestedByName;
    private Long approvedById;
    private String approvedByName;
    private LocalDateTime approvedAt;
    private Long receivedById;
    private String receivedByName;

    // Items
    private List<TransferItemDTO> items;

    // Audit
    private String notes;
    private Long createdById;
    private String createdByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}