package com.thuannluit.student_management.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthEvent {
    private UUID eventId;
    private String action;       // USER_REGISTERED, ADMIN_REGISTERED
    private String email;
    private String role;
    private Instant timestamp;
}
