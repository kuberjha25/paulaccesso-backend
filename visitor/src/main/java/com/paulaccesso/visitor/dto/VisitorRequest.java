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

    @Email(message = "Invalid email format")
    private String email;

    private String company;
    private String address;

    @NotBlank(message = "Person to meet is required")
    private String personToMeetEmpId;

    @NotBlank(message = "Purpose is required")
    private String purpose;

    private String photo;
    private String idProof;
    private String tagNumber;
}