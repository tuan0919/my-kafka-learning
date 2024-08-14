package com.nlu.app.consumer;

import com.nlu.app.dto.kafka.PayTMStatusDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaMessageListener {
    Logger log = LoggerFactory.getLogger(KafkaMessageListener.class);

//    @KafkaListener(topics = {"paytm-topic"})
//    public void consume(String message) {
//        log.info("Consumer consume message {}", message);
//    }

    @KafkaListener(topics = {"paytm-topic-1"})
    public void consume (PayTMStatusDTO status) {
        log.info("Consumer consume status {}", status);
    }
}
