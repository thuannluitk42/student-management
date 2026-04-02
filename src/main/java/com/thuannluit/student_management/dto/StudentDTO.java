package com.thuannluit.student_management.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;
import java.util.UUID;

public record StudentDTO(
        String firstName,
        String lastName,
        String email,
        LocalDate dateOfBirth
) {}
