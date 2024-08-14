package com.nlu.app.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaMessagePublisher {
    final KafkaTemplate<String, Object> template;

    public KafkaMessagePublisher(KafkaTemplate<String, Object> template) {
        this.template = template;
    }

    public void sendMessageToTopic(String message) {
        CompletableFuture<SendResult<String, Object>> result =
                template.send("paytm-topic", message);
        result.whenComplete((resp, ex) -> {
            if (ex == null) {
                System.out.println("Message sent successfully.");
                System.out.println("Sent msg = [" + message + "] with offset = ["
                        + resp.getRecordMetadata().offset()+"]");
            } else {
                System.err.println("Error sending message: " + ex.getMessage());
            }
        });
    }
}
