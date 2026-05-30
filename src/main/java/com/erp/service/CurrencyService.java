package com.erp.service;

import com.erp.dto.CurrencyDTO;
import com.erp.entity.Currency;
import com.erp.mapper.CurrencyMapper;
import com.erp.repository.CurrencyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyService {

    private final CurrencyRepository currencyRepository;
    private final CurrencyMapper currencyMapper;  // ← Add this

    @Transactional(readOnly = true)
    public List<CurrencyDTO> getAllCurrencies() {
        return currencyRepository.findByIsActiveTrue()
                .stream()
                .map(currencyMapper::toDTO)  // ← Use mapper
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CurrencyDTO getCurrencyByCode(String code) {
        Currency currency = currencyRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Currency not found: " + code));
        return currencyMapper.toDTO(currency);  // ← Use mapper
    }

    @Transactional(readOnly = true)
    public CurrencyDTO getBaseCurrency() {
        Currency currency = currencyRepository.findByIsBaseTrue()
                .orElseThrow(() -> new RuntimeException("Base currency not found"));
        return currencyMapper.toDTO(currency);  // ← Use mapper
    }

    @Transactional
    public CurrencyDTO updateExchangeRate(String code, BigDecimal rate) {
        Currency currency = currencyRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Currency not found: " + code));
        currency.setExchangeRate(rate);
        Currency saved = currencyRepository.save(currency);
        log.info("Exchange rate for {} updated to {}", code, rate);
        return currencyMapper.toDTO(saved);  // ← Use mapper
    }
}