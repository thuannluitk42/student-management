package com.thuannluit.student_management.dto.reponse;

public record LoginResponse(
        String accessToken,
        String email
) {}
