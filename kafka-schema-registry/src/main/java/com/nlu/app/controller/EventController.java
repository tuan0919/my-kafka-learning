package com.nlu.app.controller;

import com.nlu.app.dto.Employee;
import com.nlu.app.producer.KafkaAvroProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EventController {
    private final KafkaAvroProducer producer;
    @PostMapping("/events")
    public String sendMessage(@RequestBody Employee e) {
        producer.send(e);
        return "Message sent!";
    }
}
