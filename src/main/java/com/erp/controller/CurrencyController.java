package com.erp.controller;

import com.erp.dto.ApiResponse;
import com.erp.dto.CurrencyDTO;
import com.erp.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/currencies")
@RequiredArgsConstructor
@Slf4j
public class CurrencyController {

    private final CurrencyService currencyService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CurrencyDTO>>> getAllCurrencies() {
        List<CurrencyDTO> currencies = currencyService.getAllCurrencies();
        return ResponseEntity.ok(ApiResponse.<List<CurrencyDTO>>builder()
                .success(true)
                .message("Currencies retrieved successfully")
                .data(currencies)
                .code(200)
                .build());
    }

    @GetMapping("/{code}")
    public ResponseEntity<ApiResponse<CurrencyDTO>> getCurrencyByCode(@PathVariable String code) {
        CurrencyDTO currency = currencyService.getCurrencyByCode(code.toUpperCase());
        return ResponseEntity.ok(ApiResponse.<CurrencyDTO>builder()
                .success(true)
                .message("Currency retrieved successfully")
                .data(currency)
                .code(200)
                .build());
    }

    @GetMapping("/base")
    public ResponseEntity<ApiResponse<CurrencyDTO>> getBaseCurrency() {
        CurrencyDTO currency = currencyService.getBaseCurrency();
        return ResponseEntity.ok(ApiResponse.<CurrencyDTO>builder()
                .success(true)
                .message("Base currency retrieved successfully")
                .data(currency)
                .code(200)
                .build());
    }

    @PutMapping("/{code}/rate")
    public ResponseEntity<ApiResponse<CurrencyDTO>> updateExchangeRate(
            @PathVariable String code,
            @RequestParam BigDecimal rate) {
        CurrencyDTO currency = currencyService.updateExchangeRate(code.toUpperCase(), rate);
        return ResponseEntity.ok(ApiResponse.<CurrencyDTO>builder()
                .success(true)
                .message("Exchange rate updated successfully")
                .data(currency)
                .code(200)
                .build());
    }
}