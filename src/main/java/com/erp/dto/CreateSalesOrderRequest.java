package com.erp.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class CreateSalesOrderRequest {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Branch ID is required")
    private Long branchId;

    private Long warehouseId;

    private LocalDate requiredDate;

    private String discountType; // PERCENTAGE or FIXED

    private BigDecimal discountValue;

    private BigDecimal shippingCost;

    private String shippingAddress;

    private String notes;

    @NotNull(message = "Order items are required")
    private List<CreateSalesOrderItemRequest> items;
}