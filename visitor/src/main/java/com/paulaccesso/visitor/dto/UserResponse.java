package com.paulaccesso.visitor.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Long id;
    private String empId;
    private String name;
    private String email;
    private String mobile;
    private String designation;
    private String photo;
    private String role;
    private boolean active;
    private LocalDateTime createdAt;
}