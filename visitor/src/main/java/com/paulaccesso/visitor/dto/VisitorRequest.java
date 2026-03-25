package com.paulaccesso.visitor.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VisitorRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Mobile is required")
    private String mobile;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    private String company;
    private String address;

    @NotBlank(message = "Person to meet is required")
    private String personToMeet; // This will be the email of the person they want to meet
    
    @NotBlank(message = "Purpose is required")
    private String purpose;
    
    private String photo;
    private String idProof;
}