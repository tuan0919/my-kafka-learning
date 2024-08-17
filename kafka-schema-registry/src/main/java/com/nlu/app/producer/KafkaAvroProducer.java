package com.nlu.app.producer;

import com.nlu.app.dto.Employee;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KafkaAvroProducer {
    private final KafkaTemplate<String, Employee> template;
    @NonFinal
    @Value("${topic.name}")
    private String topic;
    public void send(Employee employee) {
        var future = template.send(topic, UUID.randomUUID().toString(), employee);
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                System.err.println("Error sending message: " + ex.getMessage());
            } else {
                System.out.println("Sent Message=[" + employee +"] with offset=" +
                        result.getRecordMetadata().offset());
            }
        });
    }
}
