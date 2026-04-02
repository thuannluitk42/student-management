package com.thuannluit.student_management.dto.request;

public record ChangePasswordRequest(
        String id,
        String currentPassword,
        String newPassword
) { }
