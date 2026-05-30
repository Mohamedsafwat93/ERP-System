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
public class UpdateSalesOrderRequest {

    private Long warehouseId;
    private LocalDate requiredDate;
    private BigDecimal discountValue;
    private BigDecimal shippingCost;
    private String shippingAddress;
    private String notes;
}