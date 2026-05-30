package com.erp.mapper;

import com.erp.dto.TaxRateDTO;
import com.erp.entity.TaxRate;
import org.springframework.stereotype.Component;

@Component
public class TaxRateMapper {

    public TaxRateDTO toDTO(TaxRate taxRate) {
        if (taxRate == null) return null;

        return TaxRateDTO.builder()
                .id(taxRate.getId())
                .countryCode(taxRate.getCountryCode())
                .taxNameAr(taxRate.getTaxNameAr())
                .taxNameEn(taxRate.getTaxNameEn())
                .rate(taxRate.getRate())
                .isDefault(taxRate.getIsDefault())
                .isActive(taxRate.getIsActive())
                .build();
    }

    public TaxRate toEntity(TaxRateDTO dto) {
        if (dto == null) return null;

        return TaxRate.builder()
                .id(dto.getId())
                .countryCode(dto.getCountryCode())
                .taxNameAr(dto.getTaxNameAr())
                .taxNameEn(dto.getTaxNameEn())
                .rate(dto.getRate())
                .isDefault(dto.getIsDefault())
                .isActive(dto.getIsActive())
                .build();
    }
}