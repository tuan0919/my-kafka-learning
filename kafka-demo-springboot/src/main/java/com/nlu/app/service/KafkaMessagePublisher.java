package com.nlu.app.service;

import com.nlu.app.dto.kafka.PayTMStatusDTO;
import com.nlu.app.dto.request.PaymentCreationRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
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

    public void createPayment(PaymentCreationRequest request) {
        String uuid = UUID.randomUUID().toString();
        var dto = PayTMStatusDTO.builder()
                .isPay(request.getIsPay())
                .transactionID(uuid)
                .username(request.getUsername())
                .build();
        template.send("paytm-topic-1", dto)
                .whenComplete((resp, ex) ->{
                    if (ex == null) {
                        System.out.println("Payment sent successfully.");
                        System.out.println("Sent payment = [" + dto + "] with offset = ["
                                + resp.getRecordMetadata().offset()+"]");
                    } else {
                        System.err.println("Error sending payment: " + ex.getMessage());
                    }
                });
    }
}
