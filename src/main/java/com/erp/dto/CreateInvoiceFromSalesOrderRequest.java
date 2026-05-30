package com.erp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateInvoiceFromSalesOrderRequest {

    @NotNull(message = "Sales Order ID is required")
    private Long salesOrderId;

    private LocalDate dueDate;

    private String paymentMethod;

    private String notes;
}