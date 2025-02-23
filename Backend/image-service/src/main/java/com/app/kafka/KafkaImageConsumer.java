package com.app.kafka;

import com.app.kafka.message.ImageDeletionMessage;
import com.app.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaImageConsumer {

	private final ImageService imageService;
	
	@KafkaListener(topics = "image-deletion-topic", groupId = "image-group")
	public void deleteImage(ImageDeletionMessage deletionMessage) {
		imageService.deleteImage(deletionMessage.imageName()).subscribe();			
	}
}
