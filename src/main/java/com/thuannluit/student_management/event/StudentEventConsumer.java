package com.thuannluit.student_management.event;

import com.thuannluit.student_management.entity.ProcessedEvent;
import com.thuannluit.student_management.repository.ProcessedEventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class StudentEventConsumer {

    private final ProcessedEventRepository processedEventRepository;

    @KafkaListener(topics = "${kafka.topic.name:student-events}")
    @Transactional
    public void listen(StudentEvent event) {
        try {
            log.info("Received student event: {}", event);

            if (isEventProcessed(event.getEventId().toString())) {
                log.warn("Duplicate event received, skipping: {}", event.getEventId());
                return;
            }

            log.info("Processing student event - Action: {}, StudentId: {}, Email: {}, Message: {}",
                    event.getAction(), event.getStudentId(), event.getEmail(), event.getMessage());

            markEventAsProcessed(event.getEventId().toString());

        } catch (Exception e) {
            log.error("Failed to process student event: {}", event, e);
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
