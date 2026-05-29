package com.erp.dto;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRequest {

    private String name;

    private String description;

    private String barcode;

    @Positive(message = "Price must be greater than 0")
    private BigDecimal price;

    @Positive(message = "Cost price must be greater than 0")
    private BigDecimal costPrice;

    private Long uomId;

    private Double weightInGrams;

    private Boolean isWeighted;

    private Long categoryId;

    private Long supplierId;

    private Integer reorderPoint;

    private BigDecimal taxRate;

    private Boolean isActive;
}