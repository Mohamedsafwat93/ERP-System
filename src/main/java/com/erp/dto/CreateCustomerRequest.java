package com.erp.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCustomerRequest {

    @NotBlank(message = "Customer name is required")
    @Size(min = 2, max = 100, message = "Name must be 2-100 characters")
    private String name;

    @Email(message = "Email must be valid format")
    private String email;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[0-9+\\-()\\s]{7,}$", message = "Invalid phone format")
    private String phone;

    private String address;
    private String city;
    private String taxNumber;

    @DecimalMin(value = "0.0", inclusive = true, message = "Credit limit must be >= 0")
    @Digits(integer = 13, fraction = 2, message = "Invalid credit limit format")
    private BigDecimal creditLimit;

    @NotNull(message = "Branch ID is required")
    private Long branchId;
}