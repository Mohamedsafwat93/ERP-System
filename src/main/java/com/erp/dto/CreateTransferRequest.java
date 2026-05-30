package com.erp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTransferRequest {

    private Long fromBranchId;
    private Long toBranchId;
    private Long fromWarehouseId;
    private Long toWarehouseId;

    private LocalDateTime expectedDate;

    private String transferReason;
    private String shippingMethod;
    private String trackingNumber;

    private String notes;

    @NotNull(message = "Transfer items are required")
    private List<CreateTransferItemRequest> items;
}