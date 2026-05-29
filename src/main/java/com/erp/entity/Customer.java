package com.erp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "customers", indexes = {
        @Index(name = "idx_customer_branch", columnList = "branch_id"),
        @Index(name = "idx_customer_email", columnList = "email")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    // Primary Key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Customer Information
    @NotBlank(message = "Customer name is required")
    @Size(min = 2, max = 100, message = "Name must be 2-100 characters")
    @Column(nullable = false, length = 100)
    private String name;

    @Email(message = "Email must be valid")
    @Column(length = 100, unique = false)
    private String email;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[0-9+\\-()\\s]{7,}$", message = "Invalid phone format")
    @Column(nullable = false, length = 20)
    private String phone;

    @Column(length = 255)
    private String address;

    @Column(length = 50)
    private String city;

    @Column(length = 20)
    private String taxNumber;

    // Credit Management
    @Column(precision = 15, scale = 2)
    @Builder.Default
    private java.math.BigDecimal creditLimit = java.math.BigDecimal.ZERO;

    // Multi-Branch Support
    @Column(name = "branch_id", nullable = false)
    private Long branchId;

    // Customer Status
    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true;

    // Audit Fields
    @Column(name = "created_by_id")
    private Long createdById;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_by_id")
    private Long updatedById;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Soft Delete
    @Builder.Default
    @Column(nullable = false)
    private Boolean isDeleted = false;
}