package com.nlu.app.controller;

import com.nlu.app.dto.request.PaymentCreationRequest;
import com.nlu.app.service.KafkaMessagePublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/producer-app")
public class EventController {
    private KafkaMessagePublisher publisher;

    @Autowired
    public void setPublisher(KafkaMessagePublisher publisher) {
        this.publisher = publisher;
    }

    @GetMapping("/publish/{message}")
    public ResponseEntity<?> publishMessage(@PathVariable String message) {
        try {
            publisher.sendMessageToTopic(message);
            return ResponseEntity.ok("message published successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("something went wrong :(");
        }
    }

    @PostMapping("/publish")
    public ResponseEntity<?> publish(@RequestBody PaymentCreationRequest request) {
        try {
            publisher.createPayment(request);
            return ResponseEntity.ok("message published successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("something went wrong :(");
        }
    }
}
