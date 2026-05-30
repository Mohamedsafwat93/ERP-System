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
@Table(name = "waste_reports")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WasteReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "report_number", nullable = false, unique = true, length = 50)
    private String reportNumber;

    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private DocumentStatus status;

    @ManyToOne
    @JoinColumn(name = "waste_type_id")
    private WasteType wasteType;

    @Column(name = "report_date")
    private LocalDateTime reportDate;

    @Column(name = "total_original_value")
    private BigDecimal totalOriginalValue;

    @Column(name = "total_write_off_value")
    private BigDecimal totalWriteOffValue;

    // Approval
    @ManyToOne
    @JoinColumn(name = "reported_by_id")
    private User reportedBy;

    @ManyToOne
    @JoinColumn(name = "approved_by_id")
    private User approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    // Audit
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

    // ✅ Add this relationship
    @OneToMany(mappedBy = "wasteReport", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<WasteItem> items = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        isDeleted = false;
        reportDate = LocalDateTime.now();
        if (totalOriginalValue == null) totalOriginalValue = BigDecimal.ZERO;
        if (totalWriteOffValue == null) totalWriteOffValue = BigDecimal.ZERO;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}