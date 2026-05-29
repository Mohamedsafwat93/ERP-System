package com.erp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_inventory", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"product_id", "branch_id"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Builder.Default
    @Column(name = "quantity_on_hand", nullable = false)
    private Integer quantityOnHand = 0;

    @Builder.Default
    @Column(name = "quantity_reserved")
    private Integer quantityReserved = 0;

    @Builder.Default
    @Column(name = "quantity_ordered")
    private Integer quantityOrdered = 0;

    @Builder.Default
    @Column(name = "reorder_point")
    private Integer reorderPoint = 0;

    @Builder.Default
    @Column(name = "reorder_quantity")
    private Integer reorderQuantity = 0;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }
}