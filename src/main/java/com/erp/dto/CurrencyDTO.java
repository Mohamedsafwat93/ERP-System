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
public class CurrencyDTO {
    private Long id;
    private String code;
    private String nameAr;
    private String nameEn;
    private String symbol;
    private Boolean isBase;
    private BigDecimal exchangeRate;
    private Boolean isActive;
}