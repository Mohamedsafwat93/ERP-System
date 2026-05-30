package com.erp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "document_status")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "document_type", nullable = false, length = 50)
    private String documentType;

    @Column(nullable = false, length = 30)
    private String code;

    @Column(name = "name_ar", nullable = false, length = 100)
    private String nameAr;

    @Column(name = "name_en", nullable = false, length = 100)
    private String nameEn;

    @Column(name = "sequence_order")
    private Integer sequenceOrder;

    @Column(name = "is_final")
    private Boolean isFinal = false;

    @Column(name = "requires_approval")
    private Boolean requiresApproval = false;

    @Column(name = "color_code", length = 10)
    private String colorCode;

    @Column(name = "is_active")
    private Boolean isActive = true;
}