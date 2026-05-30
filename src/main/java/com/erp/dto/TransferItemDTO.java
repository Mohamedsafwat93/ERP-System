package com.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferItemDTO {
    private Long id;
    private Long transferOrderId;

    private Long productId;
    private String productName;
    private String productSku;

    private Long uomId;
    private String uomCode;
    private String uomName;

    private Integer quantityTransferred;
    private Integer quantityReceived;
    private BigDecimal unitCost;
    private BigDecimal totalValue;

    private String batchNumber;
    private LocalDateTime expiryDate;

    private String notes;
}