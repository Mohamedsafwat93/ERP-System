package com.erp.dto;

import java.util.List;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryDTO {
    // Sales Summary
    private Integer todayOrders;
    private Integer weekOrders;
    private Integer monthOrders;
    private BigDecimal todaySales;
    private BigDecimal weekSales;
    private BigDecimal monthSales;

    // Customer Summary
    private Integer totalCustomers;
    private Integer newCustomersThisMonth;

    // Product Summary
    private Integer totalProducts;
    private Integer lowStockProducts;
    private Integer outOfStockProducts;

    // Financial Summary
    private BigDecimal totalReceivables;
    private BigDecimal totalPayables;
    private BigDecimal cashInHand;
    private BigDecimal bankBalance;

    // Recent Activities
    private List<RecentActivityDTO> recentActivities;
    private List<LowStockAlertDTO> lowStockAlerts;
}