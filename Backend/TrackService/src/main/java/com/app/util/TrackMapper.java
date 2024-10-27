package com.app.util;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.app.model.Track;
import com.app.model.projection.CreateTrackDto;
import com.app.model.projection.ResponseTrack;

@Component
public class TrackMapper {

	private final String AUDIO_URL = "http://localhost:8080/api/audio/%s";
	
	public Track fromCreateTrackDtoToTrack(CreateTrackDto dto, Integer userId) {
		return Track.builder()
				.title(dto.getTitle())
				.audioName(UUID.randomUUID().toString())
				.authorId(dto.getAuthorId())
				.playlistId(dto.getPlaylistId())
				.createdBy(userId)
				.build();
	}
	
	public ResponseTrack fromTrackToResponseTrack(Track track) {
		return ResponseTrack.builder()
				.title(track.getTitle())
				.audioUrl(String.format(AUDIO_URL, track.getAudioName()))
				.playlistId(track.getPlaylistId())
				.authorId(track.getAuthorId())
				.build();
	}
	
}
