package com.erp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "waste_audit")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WasteAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "waste_item_id")
    private WasteItem wasteItem;

    @Column(length = 50)
    private String action;

    @ManyToOne
    @JoinColumn(name = "action_by_id")
    private User actionBy;

    @Column(name = "action_at")
    private LocalDateTime actionAt;

    @Column(length = 500)
    private String notes;

    @PrePersist
    protected void onCreate() {
        actionAt = LocalDateTime.now();
    }
}