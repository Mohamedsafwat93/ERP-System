package com.erp.controller;

import com.erp.dto.ApiResponse;
import com.erp.dto.TaxRateDTO;
import com.erp.service.TaxRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/tax-rates")
@RequiredArgsConstructor
@Slf4j
public class TaxRateController {

    private final TaxRateService taxRateService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TaxRateDTO>>> getAllTaxRates() {
        List<TaxRateDTO> taxRates = taxRateService.getAllTaxRates();
        return ResponseEntity.ok(ApiResponse.<List<TaxRateDTO>>builder()
                .success(true)
                .message("Tax rates retrieved successfully")
                .data(taxRates)
                .code(200)
                .build());
    }

    @GetMapping("/{countryCode}")
    public ResponseEntity<ApiResponse<TaxRateDTO>> getTaxRateByCountry(@PathVariable String countryCode) {
        TaxRateDTO taxRate = taxRateService.getTaxRateByCountry(countryCode);
        return ResponseEntity.ok(ApiResponse.<TaxRateDTO>builder()
                .success(true)
                .message("Tax rate retrieved successfully")
                .data(taxRate)
                .code(200)
                .build());
    }

    @GetMapping("/default")
    public ResponseEntity<ApiResponse<TaxRateDTO>> getDefaultTaxRate() {
        TaxRateDTO taxRate = taxRateService.getDefaultTaxRate();
        return ResponseEntity.ok(ApiResponse.<TaxRateDTO>builder()
                .success(true)
                .message("Default tax rate retrieved successfully")
                .data(taxRate)
                .code(200)
                .build());
    }

    @PutMapping("/{countryCode}/rate")
    public ResponseEntity<ApiResponse<TaxRateDTO>> updateTaxRate(
            @PathVariable String countryCode,
            @RequestParam BigDecimal rate) {
        TaxRateDTO taxRate = taxRateService.updateTaxRate(countryCode.toUpperCase(), rate);
        return ResponseEntity.ok(ApiResponse.<TaxRateDTO>builder()
                .success(true)
                .message("Tax rate updated successfully")
                .data(taxRate)
                .code(200)
                .build());
    }

    @PutMapping("/default/{countryCode}")
    public ResponseEntity<ApiResponse<TaxRateDTO>> setDefaultTaxRate(@PathVariable String countryCode) {
        TaxRateDTO taxRate = taxRateService.setDefaultTaxRate(countryCode.toUpperCase());
        return ResponseEntity.ok(ApiResponse.<TaxRateDTO>builder()
                .success(true)
                .message("Default tax rate set successfully")
                .data(taxRate)
                .code(200)
                .build());
    }
}