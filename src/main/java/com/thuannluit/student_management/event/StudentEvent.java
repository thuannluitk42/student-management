package com.thuannluit.student_management.event;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class StudentEvent {

    private String action;       // CREATED, UPDATED, DELETED
    private String studentId;
    private String email;
    private String message;
    private Instant timestamp;
}