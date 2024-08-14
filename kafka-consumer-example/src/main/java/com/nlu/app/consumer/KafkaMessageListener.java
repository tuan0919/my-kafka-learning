package com.nlu.app.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaMessageListener {
    Logger log = LoggerFactory.getLogger(KafkaMessageListener.class);

    @KafkaListener(topics = {"paytm-topic"})
    public void consume(String message) {
        log.info("Consumer consume message {}", message);
    }
}
