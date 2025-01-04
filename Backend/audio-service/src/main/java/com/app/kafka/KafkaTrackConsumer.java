package com.app.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.app.kafka.message.TrackDeletionMessage;
import com.app.service.AudioService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KafkaTrackConsumer {

	private final AudioService audioService;
	
	@KafkaListener(topics = "track-deletion-topic", groupId = "audio-group")
	public void consumeTrackDeletionMessage(TrackDeletionMessage deletionMessage) {
		audioService.deleteAudio(deletionMessage.audioName()).subscribe();
	}
	
}
