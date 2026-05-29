package com.erp.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSupplierRequest {

    @NotBlank(message = "Supplier name is required")
    @Size(min = 2, max = 100)
    private String name;

    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[0-9+\\-()\\s]{7,}$")
    private String phone;

    private String address;
    private String city;
    private String taxNumber;
    private String paymentTerms;

    @NotNull(message = "Branch ID is required")
    private Long branchId;
}