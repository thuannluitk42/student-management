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
public class StudentEvent {

    private UUID eventId;
    private String action;       // CREATED, UPDATED, DELETED
    private String studentId;
    private String email;
    private String message;
    private Instant timestamp;
}
