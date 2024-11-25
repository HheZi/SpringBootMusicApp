package com.app.util;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.app.model.Track;
import com.app.payload.request.CreateTrackDto;
import com.app.payload.response.ResponseTrack;
import com.mpatric.mp3agic.Mp3File;

@Component
public class TrackMapper {

	private final String AUDIO_URL = "http://localhost:8080/api/audio/%s";
	
	public Track fromCreateTrackDtoToTrack(CreateTrackDto dto, Integer userId, Mp3File mp3File) {
		return Track.builder()
				.title(dto.getTitle())
				.audioName(UUID.randomUUID())
				.authorId(dto.getAuthorId())
				.albumId(dto.getAlbumId())
				.createdBy(userId)
				.duration(calculateDurationOfTrack(mp3File.getLengthInSeconds()))
				.build();
	}
	
	private String calculateDurationOfTrack(long lengthInSeconds) {
		int minutes = (int) lengthInSeconds / 60;
		int seconds = (int) lengthInSeconds % 60;
		
		return String.format("%d:%02d", minutes, seconds);
	}
	
	public ResponseTrack fromTrackToResponseTrack(Track track) {
		return ResponseTrack.builder()
				.id(track.getId())
				.title(track.getTitle())
				.audioUrl(String.format(AUDIO_URL, track.getAudioName().toString()))
				.albumId(track.getAlbumId())
				.authorId(track.getAuthorId())
				.duration(track.getDuration())
				.build();
	}
	
}
