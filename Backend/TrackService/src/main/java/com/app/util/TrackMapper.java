package com.app.util;

import org.springframework.stereotype.Component;

import com.app.model.Track;
import com.app.model.projection.CreateTrackDto;

@Component
public class TrackMapper {

	public Track fromCreateTrackDtoToTrack(CreateTrackDto dto) {
		return Track.builder()
				.title(dto.getTitle())
				.playlistId(dto.getPlaylistId())
				.build();
	}
	
}
