package com.example.kafka.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.RetryException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SendKafkaMessage {

    @Autowired
    KafkaTemplate<Object,Object> kafkaTemplate;

    @Value("${kafka.topic.two}")
    String topic2;

    @Retryable(value = RetryException.class, backoff = @Backoff(delay = 5000))
    public void sendMessage(String message){
        long startMs = System.currentTimeMillis();
        try{
            //kafka send message to topic 2
            kafkaTemplate.send(topic2,"KEY",message);
            long endMs = System.currentTimeMillis() - startMs;
            log.info("message sent");
            log.info("time took to send message "+endMs);
        } catch (Exception ex){
            throw new RetryException("Failed to send Kafka Message");
        }
    }

    @Recover
    public void recover(RetryException ex){
        log.error("Failed to send Kafka Message");
    }
}
