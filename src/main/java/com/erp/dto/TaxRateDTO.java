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
public class TaxRateDTO {
    private Long id;
    private String countryCode;
    private String taxNameAr;
    private String taxNameEn;
    private BigDecimal rate;
    private Boolean isDefault;
    private Boolean isActive;
}