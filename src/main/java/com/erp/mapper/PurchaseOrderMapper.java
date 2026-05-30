package com.erp.mapper;

import com.erp.dto.*;
import com.erp.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PurchaseOrderMapper {

    private static final DateTimeFormatter PO_NUMBER_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public PurchaseOrderDTO toDTO(PurchaseOrder order) {
        if (order == null) return null;

        PurchaseOrderDTO.PurchaseOrderDTOBuilder builder = PurchaseOrderDTO.builder()
                .id(order.getId())
                .poNumber(order.getPoNumber())
                .orderDate(order.getOrderDate())
                .expectedDate(order.getExpectedDate())
                .receivedDate(order.getReceivedDate())
                .subtotal(order.getSubtotal())
                .discountAmount(order.getDiscountAmount())
                .taxRate(order.getTaxRate())
                .taxAmount(order.getTaxAmount())
                .shippingCost(order.getShippingCost())
                .totalAmount(order.getTotalAmount())
                .paymentTerms(order.getPaymentTerms())
                .paymentStatus(order.getPaymentStatus())
                .notes(order.getNotes())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt());

        if (order.getSupplier() != null) {
            builder.supplierId(order.getSupplier().getId())
                    .supplierName(order.getSupplier().getName())
                    .supplierPhone(order.getSupplier().getPhone());
        }

        if (order.getBranch() != null) {
            builder.branchId(order.getBranch().getId())
                    .branchName(order.getBranch().getName());
        }

        if (order.getWarehouse() != null) {
            builder.warehouseId(order.getWarehouse().getId())
                    .warehouseName(order.getWarehouse().getNameAr());
        }

        if (order.getStatus() != null) {
            builder.statusId(order.getStatus().getId())
                    .statusCode(order.getStatus().getCode())
                    .statusNameAr(order.getStatus().getNameAr())
                    .statusNameEn(order.getStatus().getNameEn());
        }

        if (order.getRequestedBy() != null) {
            builder.requestedById(order.getRequestedBy().getId())
                    .requestedByName(order.getRequestedBy().getUsername());
        }
        if (order.getApprovedBy() != null) {
            builder.approvedById(order.getApprovedBy().getId())
                    .approvedByName(order.getApprovedBy().getUsername())
                    .approvedAt(order.getApprovedAt());
        }
        if (order.getCreatedBy() != null) {
            builder.createdById(order.getCreatedBy().getId())
                    .createdByName(order.getCreatedBy().getUsername());
        }

        if (order.getItems() != null && !order.getItems().isEmpty()) {
            builder.items(order.getItems().stream()
                    .map(this::toItemDTO)
                    .collect(Collectors.toList()));
        } else {
            builder.items(new ArrayList<>());
        }

        return builder.build();
    }

    public PurchaseOrderItemDTO toItemDTO(PurchaseOrderItem item) {
        if (item == null) return null;

        PurchaseOrderItemDTO.PurchaseOrderItemDTOBuilder builder = PurchaseOrderItemDTO.builder()
                .id(item.getId())
                .purchaseOrderId(item.getPurchaseOrder() != null ? item.getPurchaseOrder().getId() : null)
                .quantity(item.getQuantity())
                .quantityReceived(item.getQuantityReceived())
                .unitCost(item.getUnitCost())
                .discountPercentage(item.getDiscountPercentage())
                .discountAmount(item.getDiscountAmount())
                .taxRate(item.getTaxRate())
                .taxAmount(item.getTaxAmount())
                .totalCost(item.getTotalCost())
                .notes(item.getNotes());

        if (item.getProduct() != null) {
            builder.productId(item.getProduct().getId())
                    .productName(item.getProduct().getName())
                    .productSku(item.getProduct().getSku());
        }

        if (item.getUom() != null) {
            builder.uomId(item.getUom().getId())
                    .uomCode(item.getUom().getCode())
                    .uomName(item.getUom().getNameAr());
        }

        return builder.build();
    }

    public String generatePONumber() {
        return "PO-" + LocalDateTime.now().format(PO_NUMBER_FORMATTER);
    }
}