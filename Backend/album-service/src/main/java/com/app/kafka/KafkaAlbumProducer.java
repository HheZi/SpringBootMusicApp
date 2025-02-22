package com.app.kafka;

import com.app.kafka.message.AlbumDeletionMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaAlbumProducer {
	
	@Value("${kafka.topic.track-topic.name}")
	private String topicName;

	private final KafkaTemplate<String, AlbumDeletionMessage> kafkaTemplate;
	
	public void sendDeleteAlbumMessage(AlbumDeletionMessage albumDeletionMessage) {
		kafkaTemplate.send(topicName, albumDeletionMessage);
	}
	
}
