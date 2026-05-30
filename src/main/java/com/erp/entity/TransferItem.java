package com.erp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transfer_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "transfer_order_id", nullable = false)
    private TransferOrder transferOrder;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "uom_id", nullable = false)
    private UnitOfMeasure uom;

    @Column(name = "quantity_transferred", nullable = false)
    private Integer quantityTransferred;

    @Column(name = "quantity_received")
    private Integer quantityReceived;

    @Column(name = "unit_cost")
    private BigDecimal unitCost;

    @Column(name = "total_value")
    private BigDecimal totalValue;

    @Column(name = "batch_number", length = 50)
    private String batchNumber;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(length = 500)
    private String notes;

    @PrePersist
    protected void onCreate() {
        if (quantityReceived == null) quantityReceived = 0;
        if (unitCost == null) unitCost = BigDecimal.ZERO;
        if (totalValue == null) totalValue = BigDecimal.ZERO;
    }
}