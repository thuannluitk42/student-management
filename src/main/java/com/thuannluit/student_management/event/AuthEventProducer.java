package com.thuannluit.student_management.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthEventProducer {

    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.auth.topic.name:auth-events}")
    private String topic;

    public void sendMessage(AuthEvent event) {
        try {
            kafkaTemplate.send(topic, event);
            log.info("Sent auth event to {}: {}", topic, event);
        } catch (RuntimeException e) {
            log.error("Failed to send auth event: {}", event, e);
        }
    }
}
