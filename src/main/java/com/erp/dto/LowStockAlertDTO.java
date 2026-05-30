package com.erp.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LowStockAlertDTO {
    private Long productId;
    private String productName;
    private Integer currentStock;
    private Integer reorderPoint;
    private String uomCode;
}