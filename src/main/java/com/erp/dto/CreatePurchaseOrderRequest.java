package com.erp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePurchaseOrderRequest {

    @NotNull(message = "Supplier ID is required")
    private Long supplierId;

    @NotNull(message = "Branch ID is required")
    private Long branchId;

    private Long warehouseId;

    private LocalDate expectedDate;

    private BigDecimal discountAmount;

    private BigDecimal shippingCost;

    private String paymentTerms;

    private String notes;

    @NotNull(message = "Order items are required")
    private List<CreatePurchaseOrderItemRequest> items;
}