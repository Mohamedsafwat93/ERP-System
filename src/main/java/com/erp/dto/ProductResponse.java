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
public class ProductResponse {
    private Long id;
    private String name;
    private String sku;
    private String description;
    private String barcode;
    private BigDecimal price;
    private BigDecimal costPrice;

    // UOM info
    private Long uomId;
    private String uomCode;
    private String uomNameAr;
    private String uomNameEn;
    private Double weightInGrams;
    private Boolean isWeighted;

    // Category info
    private Long categoryId;
    private String categoryName;

    // Supplier info
    private Long supplierId;
    private String supplierName;

    private Integer reorderPoint;
    private BigDecimal taxRate;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}