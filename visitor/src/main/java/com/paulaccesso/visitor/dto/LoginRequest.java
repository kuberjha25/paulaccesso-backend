package com.paulaccesso.visitor.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Employee ID is required")
    private String empId;
}