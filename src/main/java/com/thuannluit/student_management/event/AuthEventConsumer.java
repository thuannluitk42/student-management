package com.thuannluit.student_management.event;

import com.thuannluit.student_management.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthEventConsumer {

    private final EmailService emailService;

    @KafkaListener(topics = "${kafka.auth.topic.name:auth-events}")
    public void listen(String eventJson) {
        log.info("Received auth event: {}", eventJson);

        try {
            String action = extractField(eventJson, "action");

            if ("USER_REGISTERED".equals(action) || "ADMIN_REGISTERED".equals(action)) {
                String email = extractField(eventJson, "email");
                if (email != null && !email.isEmpty()) {
                    emailService.sendWelcomeEmail(email, email);
                }
            }
        } catch (Exception e) {
            log.error("Failed to process auth event: {}", eventJson, e);
        }
    }

    private String extractField(String json, String field) {
        try {
            String key = "\"" + field + "\":\"";
            int start = json.indexOf(key);
            if (start == -1) return null;
            start += key.length();
            int end = json.indexOf("\"", start);
            return json.substring(start, end);
        } catch (Exception e) {
            return null;
        }
    }
}
