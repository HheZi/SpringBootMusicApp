package com.app.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.app.kafka.message.TrackDeletionMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
public class KafkaTrackProducer {
	
	@Value("${kafka.topic.name}")
	private String TOPIC_NAME;
	private final KafkaTemplate<String, TrackDeletionMessage> kafkaTemplate;
	
	public void sendMessage(TrackDeletionMessage message) {
		kafkaTemplate.send(TOPIC_NAME, message);
	}
	
}
