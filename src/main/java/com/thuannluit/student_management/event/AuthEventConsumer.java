package com.thuannluit.student_management.event;

import com.thuannluit.student_management.entity.ProcessedEvent;
import com.thuannluit.student_management.repository.ProcessedEventRepository;
import com.thuannluit.student_management.service.EmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthEventConsumer {

    private final EmailService emailService;
    private final ProcessedEventRepository processedEventRepository;

    @KafkaListener(topics = "${kafka.auth.topic.name:auth-events}", groupId = "auth-group")
    @Transactional
    public void listen(AuthEvent event) {
        try {
            log.info("Received auth event: {}", event);

            if (isEventProcessed(event.getEventId().toString())) {
                log.warn("Duplicate event received, skipping: {}", event.getEventId());
                return;
            }

            if ("USER_REGISTERED".equals(event.getAction()) || "ADMIN_REGISTERED".equals(event.getAction())) {
                if (event.getEmail() != null && !event.getEmail().isEmpty()) {
                    emailService.sendWelcomeEmail(event.getEmail(), event.getEmail());
                }
            }

            markEventAsProcessed(event.getEventId().toString());

        } catch (Exception e) {
            log.error("Failed to process auth event: {}", event, e);
        }
    }

    private boolean isEventProcessed(String eventId) {
        return processedEventRepository.existsById(eventId);
    }

    private void markEventAsProcessed(String eventId) {
        ProcessedEvent processedEvent = new ProcessedEvent(eventId, Instant.now());
        processedEventRepository.save(processedEvent);
        log.debug("Marked event as processed: {}", eventId);
    }
}
