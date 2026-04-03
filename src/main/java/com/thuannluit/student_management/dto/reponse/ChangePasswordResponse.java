package com.thuannluit.student_management.dto.reponse;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response object after changing password")
public record ChangePasswordResponse(

        @Schema(
                description = "Result message",
                example = "Password changed successfully"
        )
        String message
) {}