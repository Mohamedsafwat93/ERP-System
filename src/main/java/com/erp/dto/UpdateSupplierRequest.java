package com.erp.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data  // ← MUST HAVE THIS
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSupplierRequest {

    @Size(min = 2, max = 100)
    private String name;

    @Email(message = "Email must be valid")
    private String email;

    @Pattern(regexp = "^[0-9+\\-()\\s]{7,}$")
    private String phone;

    private String address;
    private String city;
    private String taxNumber;
    private String paymentTerms;
    private Boolean isActive;
}