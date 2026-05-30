package com.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePurchaseOrderRequest {
    private Long warehouseId;
    private LocalDate expectedDate;
    private BigDecimal discountAmount;
    private BigDecimal shippingCost;
    private String paymentTerms;
    private String notes;
}