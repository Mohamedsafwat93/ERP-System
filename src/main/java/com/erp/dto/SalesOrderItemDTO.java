package com.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesOrderItemDTO {
    private Long id;
    private Long salesOrderId;

    // Product info
    private Long productId;
    private String productName;
    private String productSku;

    // UOM info
    private Long uomId;
    private String uomCode;
    private String uomName;

    // Quantities
    private Integer quantity;
    private Integer quantityShipped;

    // Pricing
    private BigDecimal unitPrice;
    private BigDecimal discountPercentage;
    private BigDecimal discountAmount;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;
    private BigDecimal totalPrice;

    private String notes;
}