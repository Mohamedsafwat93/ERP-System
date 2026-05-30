package com.erp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateWasteReportRequest {

    @NotNull(message = "Branch ID is required")
    private Long branchId;

    private Long warehouseId;

    @NotNull(message = "Waste type code is required")
    private String wasteTypeCode;

    private String notes;

    @NotNull(message = "Waste items are required")
    private List<CreateWasteItemRequest> items;
}