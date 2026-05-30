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
public class WasteItemDTO {
    private Long id;
    private Long wasteReportId;

    private Long productId;
    private String productName;
    private String productSku;

    private Long uomId;
    private String uomCode;
    private String uomName;

    private Integer quantity;
    private BigDecimal originalUnitCost;
    private BigDecimal originalTotalValue;
    private BigDecimal writeOffValue;

    private String batchNumber;
    private LocalDateTime expiryDate;
    private String conditionAtDiscovery;
    private String disposalMethod;

    private String notes;
}