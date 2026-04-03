package com.thuannluit.student_management.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request object for changing user password")
public record ChangePasswordRequest(

        @Schema(
                description = "User ID",
                example = "123",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "User ID is required")
        String id,

        @Schema(
                description = "Current password",
                example = "oldPassword123",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Current password is required")
        String currentPassword,

        @Schema(
                description = "New password (min 6 characters)",
                example = "newPassword123",
                minLength = 6,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "New password is required")
        @Size(min = 6, message = "New password must be at least 6 characters")
        String newPassword
) {}