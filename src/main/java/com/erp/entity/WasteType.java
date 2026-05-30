package com.erp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "waste_types")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WasteType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 30)
    private String code;

    @Column(name = "name_ar", length = 100)
    private String nameAr;

    @Column(name = "name_en", length = 100)
    private String nameEn;

    @Column(length = 50)
    private String category;

    @Column(name = "gl_account_code", length = 20)
    private String glAccountCode;

    @Column(name = "is_active")
    private Boolean isActive = true;
}