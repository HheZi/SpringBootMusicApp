package com.app.kafka.producer;

import com.app.kafka.message.ImageDeletionMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaImageProducer {

	@Value("${kafka.topic.name}")
	private String topicName;
	
	private final KafkaTemplate<String, ImageDeletionMessage> kafkaTemplate;
	
	public void sendMessageToDeleteImage(ImageDeletionMessage deletionMessage) {
		kafkaTemplate.send(topicName, deletionMessage);
	}
	
}