package com.erp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "invoice_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @ManyToOne
    @JoinColumn(name = "sales_order_item_id")
    private SalesOrderItem salesOrderItem;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "uom_id", nullable = false)
    private UnitOfMeasure uom;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "discount_percentage")
    private BigDecimal discountPercentage;

    @Column(name = "discount_amount")
    private BigDecimal discountAmount;

    @Column(name = "tax_rate")
    private BigDecimal taxRate;

    @Column(name = "tax_amount")
    private BigDecimal taxAmount;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @Column(length = 500)
    private String notes;

    @PrePersist
    protected void onCreate() {
        if (discountPercentage == null) discountPercentage = BigDecimal.ZERO;
        if (discountAmount == null) discountAmount = BigDecimal.ZERO;
        if (taxRate == null) taxRate = new BigDecimal("15.00");
        if (taxAmount == null) taxAmount = BigDecimal.ZERO;
    }
}