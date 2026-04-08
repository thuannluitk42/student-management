package com.thuannluit.student_management.event;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class AuthEvent {
    private String action;       // USER_REGISTERED, ADMIN_REGISTERED
    private String email;
    private String role;
    private Instant timestamp;
}
