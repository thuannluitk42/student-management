package com.thuannluit.student_management.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request object for user login")
public record LoginRequest(

        @Schema(
                description = "User email address",
                example = "user@example.com",
                format = "email",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @Schema(
                description = "User password",
                example = "123456",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Password is required")
        String password
) {}