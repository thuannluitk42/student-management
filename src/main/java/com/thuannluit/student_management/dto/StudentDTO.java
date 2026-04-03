package com.thuannluit.student_management.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Student Data Transfer Object")
public record StudentDTO(

        @Schema(
                description = "First name of the student",
                example = "John",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String firstName,

        @Schema(
                description = "Last name of the student",
                example = "Doe",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String lastName,

        @Schema(
                description = "Email address",
                example = "john.doe@example.com",
                format = "email",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String email,

        @Schema(
                description = "Date of birth (ISO format)",
                example = "2000-01-01",
                type = "string",
                format = "date",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        LocalDate dateOfBirth
) {}