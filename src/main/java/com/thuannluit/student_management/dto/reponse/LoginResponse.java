package com.thuannluit.student_management.dto.reponse;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response object returned after successful authentication")
public record LoginResponse(

        @Schema(
                description = "JWT access token",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
        )
        String accessToken,

        @Schema(
                description = "User email",
                example = "user@example.com"
        )
        String email
) {}