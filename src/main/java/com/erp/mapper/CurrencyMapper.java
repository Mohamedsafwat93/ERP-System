package com.erp.mapper;

import com.erp.dto.CurrencyDTO;
import com.erp.entity.Currency;
import org.springframework.stereotype.Component;

@Component
public class CurrencyMapper {

    public CurrencyDTO toDTO(Currency currency) {
        if (currency == null) return null;

        return CurrencyDTO.builder()
                .id(currency.getId())
                .code(currency.getCode())
                .nameAr(currency.getNameAr())
                .nameEn(currency.getNameEn())
                .symbol(currency.getSymbol())
                .isBase(currency.getIsBase())
                .exchangeRate(currency.getExchangeRate())
                .isActive(currency.getIsActive())
                .build();
    }

    public Currency toEntity(CurrencyDTO dto) {
        if (dto == null) return null;

        return Currency.builder()
                .id(dto.getId())
                .code(dto.getCode())
                .nameAr(dto.getNameAr())
                .nameEn(dto.getNameEn())
                .symbol(dto.getSymbol())
                .isBase(dto.getIsBase())
                .exchangeRate(dto.getExchangeRate())
                .isActive(dto.getIsActive())
                .build();
    }
}