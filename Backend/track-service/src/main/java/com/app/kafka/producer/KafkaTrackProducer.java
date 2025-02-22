package com.app.kafka.producer;

import com.app.kafka.message.TrackDeletionMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaTrackProducer {
	
	@Value("${kafka.topic.name}")
	private String topicName;
	
	private final KafkaTemplate<String, TrackDeletionMessage> kafkaTemplate;
	
	public void sendMessage(TrackDeletionMessage message) {
		kafkaTemplate.send(topicName, message);
	}
	
}
