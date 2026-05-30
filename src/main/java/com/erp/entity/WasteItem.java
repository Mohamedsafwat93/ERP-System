package com.erp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "waste_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WasteItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "waste_report_id", nullable = false)
    private WasteReport wasteReport;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "uom_id", nullable = false)
    private UnitOfMeasure uom;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "original_unit_cost")
    private BigDecimal originalUnitCost;

    @Column(name = "original_total_value")
    private BigDecimal originalTotalValue;

    @Column(name = "current_unit_value")
    private BigDecimal currentUnitValue;

    @Column(name = "write_off_value")
    private BigDecimal writeOffValue;

    @Column(name = "batch_number", length = 50)
    private String batchNumber;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "condition_at_discovery", length = 500)
    private String conditionAtDiscovery;

    @Column(name = "disposal_method", length = 50)
    private String disposalMethod;

    @Column(length = 500)
    private String notes;

    @PrePersist
    protected void onCreate() {
        if (originalUnitCost == null) originalUnitCost = BigDecimal.ZERO;
        if (originalTotalValue == null) originalTotalValue = BigDecimal.ZERO;
        if (currentUnitValue == null) currentUnitValue = BigDecimal.ZERO;
        if (writeOffValue == null) writeOffValue = BigDecimal.ZERO;
    }
}