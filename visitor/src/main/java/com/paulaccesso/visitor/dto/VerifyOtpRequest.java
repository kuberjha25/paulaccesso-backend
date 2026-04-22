package com.paulaccesso.visitor.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyOtpRequest {
    @NotBlank(message = "Employee ID is required")
    private String empId;

    @NotBlank(message = "OTP is required")
    private String otp;
}