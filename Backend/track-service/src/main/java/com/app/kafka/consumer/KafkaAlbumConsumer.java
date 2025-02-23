package com.app.kafka.consumer;

import com.app.kafka.message.AlbumDeletionMessage;
import com.app.service.TrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaAlbumConsumer {

	private final TrackService service;
	
	@KafkaListener(topics = "album-deletion-topic", groupId = "track-group")
	public void deleteTracksByAlbumId(AlbumDeletionMessage albumDeletionMessage) {
		service.deleteTracksByAlbumId(albumDeletionMessage.id())
		.subscribe();
	}
	
}
