package com.erp.service;

import com.erp.dto.TaxRateDTO;
import com.erp.entity.TaxRate;
import com.erp.mapper.TaxRateMapper;
import com.erp.repository.TaxRateRepository;
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
public class TaxRateService {

    private final TaxRateRepository taxRateRepository;
    private final TaxRateMapper taxRateMapper;  // ← Add this

    @Transactional(readOnly = true)
    public List<TaxRateDTO> getAllTaxRates() {
        return taxRateRepository.findByIsActiveTrue()
                .stream()
                .map(taxRateMapper::toDTO)  // ← Use mapper
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TaxRateDTO getTaxRateByCountry(String countryCode) {
        TaxRate taxRate = taxRateRepository.findByCountryCode(countryCode.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Tax rate not found for country: " + countryCode));
        return taxRateMapper.toDTO(taxRate);  // ← Use mapper
    }

    @Transactional(readOnly = true)
    public TaxRateDTO getDefaultTaxRate() {
        TaxRate taxRate = taxRateRepository.findByIsDefaultTrue()
                .orElseThrow(() -> new RuntimeException("Default tax rate not found"));
        return taxRateMapper.toDTO(taxRate);  // ← Use mapper
    }

    @Transactional
    public TaxRateDTO updateTaxRate(String countryCode, BigDecimal rate) {
        TaxRate taxRate = taxRateRepository.findByCountryCode(countryCode.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Tax rate not found for country: " + countryCode));
        taxRate.setRate(rate);
        TaxRate saved = taxRateRepository.save(taxRate);
        log.info("Tax rate for {} updated to {}%", countryCode, rate);
        return taxRateMapper.toDTO(saved);  // ← Use mapper
    }

    @Transactional
    public TaxRateDTO setDefaultTaxRate(String countryCode) {
        List<TaxRate> allRates = taxRateRepository.findAll();
        allRates.forEach(rate -> rate.setIsDefault(false));
        taxRateRepository.saveAll(allRates);

        TaxRate newDefault = taxRateRepository.findByCountryCode(countryCode.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Tax rate not found for country: " + countryCode));
        newDefault.setIsDefault(true);

        log.info("Default tax rate set to {}", countryCode);
        return taxRateMapper.toDTO(taxRateRepository.save(newDefault));  // ← Use mapper
    }
}