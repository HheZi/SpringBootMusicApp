package com.app.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.app.kafka.message.TrackDeletionMessage;
import com.app.service.PlaylistService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KafkaTrackConsumer {
	
	private final PlaylistService playlistService;
	
	@KafkaListener(topics = "track-deletion-topic", groupId = "track-deletion-group")
	public void consumeTrackDeletionMessage(TrackDeletionMessage deletionMessage) { 
		playlistService.deleteTrackFromAllPlaylists(deletionMessage);
	}
	
}
