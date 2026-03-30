package com.paulaccesso.visitor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TagAssignmentRequest {
    @NotNull(message = "Visitor ID is required")
    private Long visitorId;
    
    @NotBlank(message = "Tag number is required")
    private String tagNumber;
}
