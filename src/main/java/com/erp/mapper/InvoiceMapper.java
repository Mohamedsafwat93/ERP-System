package com.erp.mapper;

import com.erp.dto.*;
import com.erp.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InvoiceMapper {

    private static final DateTimeFormatter INVOICE_NUMBER_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public InvoiceDTO toDTO(Invoice invoice) {
        if (invoice == null) return null;

        InvoiceDTO.InvoiceDTOBuilder builder = InvoiceDTO.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .invoiceDate(invoice.getInvoiceDate())
                .dueDate(invoice.getDueDate())
                .subtotal(invoice.getSubtotal())
                .discountAmount(invoice.getDiscountAmount())
                .taxRate(invoice.getTaxRate())
                .taxAmount(invoice.getTaxAmount())
                .totalAmount(invoice.getTotalAmount())
                .amountPaid(invoice.getAmountPaid())
                .balanceDue(invoice.getBalanceDue())
                .paymentMethod(invoice.getPaymentMethod())
                .paymentStatus(invoice.getPaymentStatus())
                .notes(invoice.getNotes())
                .createdAt(invoice.getCreatedAt())
                .updatedAt(invoice.getUpdatedAt());

        // Sales Order info
        if (invoice.getSalesOrder() != null) {
            builder.salesOrderId(invoice.getSalesOrder().getId())
                    .salesOrderNumber(invoice.getSalesOrder().getOrderNumber());
        }

        // Customer info
        if (invoice.getCustomer() != null) {
            builder.customerId(invoice.getCustomer().getId())
                    .customerName(invoice.getCustomer().getName())
                    .customerPhone(invoice.getCustomer().getPhone())
                    .customerEmail(invoice.getCustomer().getEmail());
        }

        // Branch info
        if (invoice.getBranch() != null) {
            builder.branchId(invoice.getBranch().getId())
                    .branchName(invoice.getBranch().getName());
        }

        // Status info
        if (invoice.getStatus() != null) {
            builder.statusId(invoice.getStatus().getId())
                    .statusCode(invoice.getStatus().getCode())
                    .statusNameAr(invoice.getStatus().getNameAr())
                    .statusNameEn(invoice.getStatus().getNameEn());
        }

        // Created by info
        if (invoice.getCreatedBy() != null) {
            builder.createdById(invoice.getCreatedBy().getId())
                    .createdByName(invoice.getCreatedBy().getUsername());
        }

        // Items
        if (invoice.getItems() != null && !invoice.getItems().isEmpty()) {
            builder.items(invoice.getItems().stream()
                    .map(this::toItemDTO)
                    .collect(Collectors.toList()));
        } else {
            builder.items(new ArrayList<>());
        }

        return builder.build();
    }

    public InvoiceItemDTO toItemDTO(InvoiceItem item) {
        if (item == null) return null;

        InvoiceItemDTO.InvoiceItemDTOBuilder builder = InvoiceItemDTO.builder()
                .id(item.getId())
                .invoiceId(item.getInvoice() != null ? item.getInvoice().getId() : null)
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .discountPercentage(item.getDiscountPercentage())
                .discountAmount(item.getDiscountAmount())
                .taxRate(item.getTaxRate())
                .taxAmount(item.getTaxAmount())
                .totalPrice(item.getTotalPrice())
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

    public String generateInvoiceNumber() {
        return "INV-" + LocalDateTime.now().format(INVOICE_NUMBER_FORMATTER);
    }
}