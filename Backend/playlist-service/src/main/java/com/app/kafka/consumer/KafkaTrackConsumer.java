package com.app.kafka.consumer;

import com.app.kafka.message.TrackDeletionMessage;
import com.app.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaTrackConsumer {
	
	private final PlaylistService playlistService;
	
	@KafkaListener(topics = "track-deletion-topic", groupId = "playlist-group")
	public void consumeTrackDeletionMessage(TrackDeletionMessage deletionMessage) { 
		playlistService.deleteTrackFromAllPlaylists(deletionMessage);
	}
	
}
