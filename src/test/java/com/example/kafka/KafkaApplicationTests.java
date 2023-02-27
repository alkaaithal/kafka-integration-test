package com.example.kafka;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import static org.awaitility.Awaitility.await;

@Slf4j
@ExtendWith({OutputCaptureExtension.class, SpringExtension.class, MockitoExtension.class})

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
				properties = {"spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}"})
@EmbeddedKafka(topics = {"${kafka.topic.one}"})
class KafkaApplicationTests {

	@Autowired
	private KafkaTemplate<Object,Object> kafkaTemplate;

	@Value("${kafka.topic.one}")
	String topic1;


	@Test
	void kafkaIntegrationTest(CapturedOutput output) {
		kafkaTemplate.send(topic1, "KEY","some-test-message");
		kafkaTemplate.flush();

		//default wait time is 10 seconds,
		// verify that flow is complete by receiving kafka message from some.other.topic.name.two
		await().until(() -> output.getOut().contains("#### Kafka: Message Received on topic 2"));
	}

	@KafkaListener(topics = {"${kafka.topic.two}"})
	public void consumeMessage(String message){
		log.info("#### Kafka: Message Received on topic 2");
	}
}
