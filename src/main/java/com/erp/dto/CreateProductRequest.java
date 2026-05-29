package com.erp.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {

    @NotBlank(message = "Product name is required")
    @Size(max = 200, message = "Product name must be less than 200 characters")
    private String name;

    private String sku;

    private String description;

    private String barcode;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than 0")
    private BigDecimal price;

    private BigDecimal costPrice;

    @NotNull(message = "UOM ID is required")
    private Long uomId;

    private Double weightInGrams;

    private Boolean isWeighted;

    private Long categoryId;

    private Long supplierId;

    private Integer reorderPoint;

    private BigDecimal taxRate;
}