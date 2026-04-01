package com.thuannluit.student_management.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @Email(message = "Email is mandatory")
        String email,
        @NotBlank(message = "password is mandatory")
        String password
) { }
