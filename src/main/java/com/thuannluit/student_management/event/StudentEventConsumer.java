package com.thuannluit.student_management.event;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class StudentEventConsumer {

    @KafkaListener(topics = "${kafka.topic.name}", groupId = "${spring.kafka.consumer.group-id:student-group}")
    public void listen(StudentEvent event) {
        log.info("Received student event - Action: {}, StudentId: {}, Email: {}, Message: {}",
                event.getAction(), event.getStudentId(), event.getEmail(), event.getMessage());
    }
}