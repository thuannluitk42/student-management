package com.thuannluit.student_management.dto.reponse;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Standard error response")
public record ErrorResponse(

        @Schema(
                description = "Error code identifying the type of error",
                example = "USER_NOT_FOUND"
        )
        String errorCode,

        @Schema(
                description = "Human-readable error message",
                example = "User not found"
        )
        String message,

        @Schema(
                description = "Timestamp when the error occurred",
                example = "2026-04-03T14:00:00"
        )
        LocalDateTime timestamp
) {

    public ErrorResponse(String errorCode, String message) {
        this(errorCode, message, LocalDateTime.now());
    }
}