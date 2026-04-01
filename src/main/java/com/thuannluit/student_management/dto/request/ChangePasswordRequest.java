package com.thuannluit.student_management.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(
        @NotBlank(message = "user id is mandatory")
        String id,
        @NotBlank(message = "current password is mandatory")
        String currentPassword,
        @NotBlank(message = "new password is mandatory")
        String newPassword

) { }
