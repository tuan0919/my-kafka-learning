package com.nlu.app;

import com.nlu.app.dto.request.PaymentCreationRequest;
import com.nlu.app.service.KafkaMessagePublisher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

import static org.awaitility.Awaitility.await;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class KafkaDemoSpringbootApplicationTests {
	@Container
	static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));

	@DynamicPropertySource
	public static void initKafkaProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
	}

	private KafkaMessagePublisher publisher;

	@Autowired
	public void setPublisher(KafkaMessagePublisher publisher) {
		this.publisher = publisher;
	}

	@Test
	public void testSendEventToTopic() {
		var request = PaymentCreationRequest.builder()
						.isPay(true).username("test-user")
						.build();
		publisher.createPayment(request);
		await().pollInterval(Duration.ofSeconds(3))
				.atMost(Duration.ofSeconds(10))
				.untilAsserted(() -> {
					// assert message sent successfully to Kafka topic
				});
	}
}
