package com.app.util;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.app.model.Track;
import com.app.payload.request.CreateTrackDto;
import com.app.payload.response.ResponseTotalDuration;
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
				.duration(mp3File.getLengthInSeconds())
				.build();
	}
	
	private String calculateDurationOfTrack(long lengthInSeconds) {
		int minutes = (int) lengthInSeconds / 60;
		int seconds = (int) lengthInSeconds % 60;
		
		return String.format("%d:%02d", minutes, seconds);
	}
	
	public ResponseTotalDuration getDurationOfTrack(long totalSeconds) {
		int hours = (int) (totalSeconds / 3600);
		
		if (totalSeconds >= 60 * 60 * 24) {
			return new ResponseTotalDuration("More then 24 hours");
		}
		else if(totalSeconds >= 60 * 60) {
			return new ResponseTotalDuration("At least "+ hours  + " hours");
		}
		else {
			return new ResponseTotalDuration(calculateDurationOfTrack(totalSeconds));
		}
	}
	
	public ResponseTrack fromTrackToResponseTrack(Track track) {
		return ResponseTrack.builder()
				.id(track.getId())
				.title(track.getTitle())
				.audioUrl(String.format(AUDIO_URL, track.getAudioName().toString()))
				.albumId(track.getAlbumId())
				.authorId(track.getAuthorId())
				.duration(calculateDurationOfTrack(track.getDuration()))
				.build();
	}
	
}
