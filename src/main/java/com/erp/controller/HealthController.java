package com.erp.controller;

import com.erp.dto.ApiResponse;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/health")
public class HealthController {

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, String>>> health() {
        Map<String, String> healthData = new HashMap<>();
        healthData.put("status", "UP");
        healthData.put("application", "ERP System");
        healthData.put("version", "1.0.0");

        return ResponseEntity.ok(
            ApiResponse.<Map<String, String>>builder()
                .success(true)
                .message("Application is healthy")
                .data(healthData)
                .code(200)
                .lang(LocaleContextHolder.getLocale().getLanguage())
                .build()
        );
    }
}