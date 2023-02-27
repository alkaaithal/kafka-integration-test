package com.example.kafka.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ReceiveKafkaMessage {

    private final SendKafkaMessage sendKafkaMessage;

    public ReceiveKafkaMessage(SendKafkaMessage sendKafkaMessage){
        this.sendKafkaMessage = sendKafkaMessage;

    }

    @KafkaListener(topics = "${kafka.topic.one}")
    public void listenKafkaMessage(String message){
        log.info("Message Received" +message);

        //once message is received on topic1 send message to topic2
        sendKafkaMessage.sendMessage("some-message");
    }
}
