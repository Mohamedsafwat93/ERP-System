package com.erp.controller;

import com.erp.dto.*;
import com.erp.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<DashboardSummaryDTO>> getDashboardSummary(
            @RequestParam(required = false) Long branchId) {

        DashboardSummaryDTO summary = dashboardService.getDashboardSummary(branchId);
        return ResponseEntity.ok(ApiResponse.success(summary, "Dashboard summary retrieved"));
    }
}