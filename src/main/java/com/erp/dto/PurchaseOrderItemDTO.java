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
public class PurchaseOrderItemDTO {
    private Long id;
    private Long purchaseOrderId;

    private Long productId;
    private String productName;
    private String productSku;

    private Long uomId;
    private String uomCode;
    private String uomName;

    private Integer quantity;
    private Integer quantityReceived;
    private BigDecimal unitCost;
    private BigDecimal discountPercentage;
    private BigDecimal discountAmount;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;
    private BigDecimal totalCost;

    private String notes;
}