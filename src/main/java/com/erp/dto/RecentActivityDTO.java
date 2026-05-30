package com.erp.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecentActivityDTO {
    private String activityType; // SALE, PURCHASE, INVOICE, TRANSFER, WASTE
    private String documentNumber;
    private String customerOrSupplierName;
    private BigDecimal amount;
    private String status;
    private LocalDateTime dateTime;
    private String userName;
}