package com.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnitOfMeasureDTO {
    private Long id;
    private String code;
    private String nameAr;
    private String nameEn;
    private String category;
    private String symbol;
    private Boolean isActive;
    private Integer sortOrder;
}