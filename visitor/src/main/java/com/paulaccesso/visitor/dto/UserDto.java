package com.paulaccesso.visitor.dto;

import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String email;
    private String name;
    private String designation;
    private String photo;
    private String role;
}