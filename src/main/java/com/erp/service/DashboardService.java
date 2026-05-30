package com.erp.service;

import java.util.List;

import com.erp.dto.*;
import com.erp.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final SalesOrderRepository salesOrderRepository;
    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final ProductInventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public DashboardSummaryDTO getDashboardSummary(Long branchId) {

        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        LocalDateTime weekStart = LocalDateTime.now().minusDays(7);
        LocalDateTime monthStart = LocalDateTime.now().minusDays(30);

        // Sales Summary
        BigDecimal todaySales = BigDecimal.ZERO;
        BigDecimal weekSales = BigDecimal.ZERO;
        BigDecimal monthSales = BigDecimal.ZERO;

        Integer todayOrders = 0;
        Integer weekOrders = 0;
        Integer monthOrders = 0;

        // Customer Summary
        Integer totalCustomers = 0;
        Integer newCustomersThisMonth = 0;

        // Product Summary
        Integer totalProducts = 0;
        Integer lowStockProducts = 0;
        Integer outOfStockProducts = 0;

        // Financial Summary
        BigDecimal totalReceivables = BigDecimal.ZERO;
        BigDecimal totalPayables = BigDecimal.ZERO;

        // Recent Activities
        List<RecentActivityDTO> recentActivities = new ArrayList<>();
        List<LowStockAlertDTO> lowStockAlerts = new ArrayList<>();

        return DashboardSummaryDTO.builder()
                .todaySales(todaySales)
                .weekSales(weekSales)
                .monthSales(monthSales)
                .todayOrders(todayOrders)
                .weekOrders(weekOrders)
                .monthOrders(monthOrders)
                .totalCustomers(totalCustomers)
                .newCustomersThisMonth(newCustomersThisMonth)
                .totalProducts(totalProducts)
                .lowStockProducts(lowStockProducts)
                .outOfStockProducts(outOfStockProducts)
                .totalReceivables(totalReceivables)
                .totalPayables(totalPayables)
                .recentActivities(recentActivities)
                .lowStockAlerts(lowStockAlerts)
                .build();
    }
}