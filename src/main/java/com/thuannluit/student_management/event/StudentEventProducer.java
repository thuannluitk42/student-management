package com.thuannluit.student_management.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class StudentEventProducer {

    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.name:student-events}")
    private String topic;

    public void sendMessage(StudentEvent event) {
        try {
            kafkaTemplate.send(topic, event);
            log.info("Sent event to {}: {}", topic, event);
        }  catch (RuntimeException e) {
            log.error("Failed to send event: {}", event, e);
        }
    }
}
