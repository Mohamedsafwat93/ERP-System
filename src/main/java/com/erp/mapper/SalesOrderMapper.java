package com.erp.mapper;

import com.erp.dto.*;
import com.erp.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SalesOrderMapper {

    private final CustomerMapper customerMapper;
    private final SupplierMapper supplierMapper;

    public SalesOrderDTO toDTO(SalesOrder order) {
        if (order == null) return null;

        SalesOrderDTO.SalesOrderDTOBuilder builder = SalesOrderDTO.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .orderDate(order.getOrderDate())
                .requiredDate(order.getRequiredDate())
                .shippingDate(order.getShippingDate())
                .subtotal(order.getSubtotal())
                .discountType(order.getDiscountType())
                .discountValue(order.getDiscountValue())
                .discountAmount(order.getDiscountAmount())
                .taxRate(order.getTaxRate())
                .taxAmount(order.getTaxAmount())
                .shippingCost(order.getShippingCost())
                .totalAmount(order.getTotalAmount())
                .shippingAddress(order.getShippingAddress())
                .trackingNumber(order.getTrackingNumber())
                .rejectionReason(order.getRejectionReason())
                .notes(order.getNotes())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt());

        // Customer info
        if (order.getCustomer() != null) {
            builder.customerId(order.getCustomer().getId())
                    .customerName(order.getCustomer().getName())
                    .customerPhone(order.getCustomer().getPhone());
        }

        // Branch info
        if (order.getBranch() != null) {
            builder.branchId(order.getBranch().getId())
                    .branchName(order.getBranch().getName());
        }

        // Warehouse info
        if (order.getWarehouse() != null) {
            builder.warehouseId(order.getWarehouse().getId())
                    .warehouseName(order.getWarehouse().getNameAr());
        }

        // Status info
        if (order.getStatus() != null) {
            builder.statusId(order.getStatus().getId())
                    .statusCode(order.getStatus().getCode())
                    .statusNameAr(order.getStatus().getNameAr())
                    .statusNameEn(order.getStatus().getNameEn());
        }

        // Approval info
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

        // ✅ ADD THIS - Include items in response
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            builder.items(order.getItems().stream()
                    .map(this::toItemDTO)
                    .collect(Collectors.toList()));
        } else {
            builder.items(new ArrayList<>());
        }

        return builder.build();
    }

    // ✅ ADD THIS - Convert SalesOrderItem to DTO
    public SalesOrderItemDTO toItemDTO(SalesOrderItem item) {
        if (item == null) return null;

        SalesOrderItemDTO.SalesOrderItemDTOBuilder builder = SalesOrderItemDTO.builder()
                .id(item.getId())
                .salesOrderId(item.getSalesOrder() != null ? item.getSalesOrder().getId() : null)
                .quantity(item.getQuantity())
                .quantityShipped(item.getQuantityShipped())
                .unitPrice(item.getUnitPrice())
                .discountPercentage(item.getDiscountPercentage())
                .discountAmount(item.getDiscountAmount())
                .taxRate(item.getTaxRate())
                .taxAmount(item.getTaxAmount())
                .totalPrice(item.getTotalPrice())
                .notes(item.getNotes());

        // Product info
        if (item.getProduct() != null) {
            builder.productId(item.getProduct().getId())
                    .productName(item.getProduct().getName())
                    .productSku(item.getProduct().getSku());
        }

        // UOM info
        if (item.getUom() != null) {
            builder.uomId(item.getUom().getId())
                    .uomCode(item.getUom().getCode())
                    .uomName(item.getUom().getNameAr());
        }

        return builder.build();
    }

    public SalesOrder toEntity(CreateSalesOrderRequest request, String orderNumber) {
        if (request == null) return null;

        return SalesOrder.builder()
                .orderNumber(orderNumber)
                .requiredDate(request.getRequiredDate())
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue() != null ? request.getDiscountValue() : BigDecimal.ZERO)
                .shippingCost(request.getShippingCost() != null ? request.getShippingCost() : BigDecimal.ZERO)
                .shippingAddress(request.getShippingAddress())
                .notes(request.getNotes())
                .orderDate(LocalDateTime.now())
                .subtotal(BigDecimal.ZERO)
                .discountAmount(BigDecimal.ZERO)
                .taxAmount(BigDecimal.ZERO)
                .totalAmount(BigDecimal.ZERO)
                .isDeleted(false)
                .build();
    }

    public SalesOrderItem toItemEntity(SalesOrder order, Product product, UnitOfMeasure uom,
                                       CreateSalesOrderItemRequest itemRequest) {
        if (itemRequest == null) return null;

        BigDecimal discountPercentage = itemRequest.getDiscountPercentage() != null ?
                itemRequest.getDiscountPercentage() : BigDecimal.ZERO;
        BigDecimal lineSubtotal = itemRequest.getUnitPrice()
                .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
        BigDecimal discountAmount = lineSubtotal
                .multiply(discountPercentage.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP));
        BigDecimal afterDiscount = lineSubtotal.subtract(discountAmount);
        BigDecimal taxAmount = afterDiscount.multiply(new BigDecimal("0.15"));
        BigDecimal totalPrice = afterDiscount.add(taxAmount);

        return SalesOrderItem.builder()
                .salesOrder(order)
                .product(product)
                .uom(uom)
                .quantity(itemRequest.getQuantity())
                .quantityShipped(0)
                .unitPrice(itemRequest.getUnitPrice())
                .discountPercentage(discountPercentage)
                .discountAmount(discountAmount)
                .taxRate(new BigDecimal("15.00"))
                .taxAmount(taxAmount)
                .totalPrice(totalPrice)
                .notes(itemRequest.getNotes())
                .build();
    }
}