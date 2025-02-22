package com.app.kafka;

import com.app.kafka.message.TrackDeletionMessage;
import com.app.service.FavoriteTrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaTrackConsumer {

	private final FavoriteTrackService service;
	
	@KafkaListener(topics = "track-deletion-topic", groupId = "favorite-group")
	public void consumeTrackDeletionMessage(TrackDeletionMessage deletionMessage) {
		service.deleteTrackFromFavorites(deletionMessage.trackId()).subscribe();
	}
	
}
