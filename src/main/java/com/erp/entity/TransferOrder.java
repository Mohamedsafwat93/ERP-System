package com.erp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "transfer_orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transfer_number", nullable = false, unique = true, length = 50)
    private String transferNumber;

    @ManyToOne
    @JoinColumn(name = "from_branch_id")
    private Branch fromBranch;

    @ManyToOne
    @JoinColumn(name = "to_branch_id")
    private Branch toBranch;

    @ManyToOne
    @JoinColumn(name = "from_warehouse_id")
    private Warehouse fromWarehouse;

    @ManyToOne
    @JoinColumn(name = "to_warehouse_id")
    private Warehouse toWarehouse;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private DocumentStatus status;

    @Column(name = "transfer_date")
    private LocalDateTime transferDate;

    @Column(name = "expected_date")
    private LocalDateTime expectedDate;

    @Column(name = "received_date")
    private LocalDateTime receivedDate;

    @Column(name = "transfer_reason", length = 100)
    private String transferReason;

    @Column(name = "shipping_method", length = 50)
    private String shippingMethod;

    @Column(name = "tracking_number", length = 100)
    private String trackingNumber;

    @Column(name = "total_value")
    private BigDecimal totalValue;

    @ManyToOne
    @JoinColumn(name = "requested_by_id")
    private User requestedBy;

    @ManyToOne
    @JoinColumn(name = "approved_by_id")
    private User approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @ManyToOne
    @JoinColumn(name = "received_by_id")
    private User receivedBy;

    @Column(length = 500)
    private String notes;

    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @OneToMany(mappedBy = "transferOrder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<TransferItem> items = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        isDeleted = false;
        if (totalValue == null) totalValue = BigDecimal.ZERO;
        if (transferDate == null) transferDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}