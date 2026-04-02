package com.thuannluit.student_management.dto.request;

public record LoginRequest(
        String email,
        String password
) {
}
