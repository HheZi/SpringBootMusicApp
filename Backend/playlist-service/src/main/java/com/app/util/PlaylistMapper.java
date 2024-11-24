package com.app.util;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.app.model.Playlist;
import com.app.payload.request.CreateOrUpdatePlaylist;
import com.app.payload.response.ResponsePlaylist;

@Component
public class PlaylistMapper {
	
	private final String IMAGE_URL_FORMAT = "http://localhost:8080/api/images/";
	
	private final String IMAGE_URL_DEFAULT = IMAGE_URL_FORMAT + "default";

	public Playlist fromCreatePlaylistToPlaylist(CreateOrUpdatePlaylist dto, Integer userId,Boolean isCoverPresent) {
		return Playlist.builder()
				.createdBy(userId)
				.description(dto.getDescription())
				.imageName(isCoverPresent ? UUID.randomUUID() : null)
				.name(dto.getName())
				.build();
	}
	
	public ResponsePlaylist fromPlaylistToResponsePlaylist(Playlist playlist, List<Long> trackIds) {
//		if (playlist == null ) return null;
		
		return ResponsePlaylist.builder()
				.id(playlist.getId())
				.description(playlist.getDescription())
				.imageUrl(playlist.getImageName() != null ? IMAGE_URL_FORMAT + playlist.getImageName() : IMAGE_URL_DEFAULT)
				.name(playlist.getName())
				.trackIds(trackIds)
				.numberOfTracks(trackIds != null ? trackIds.size() : null)
				.build();
	}
	public ResponsePlaylist fromPlaylistToResponsePlaylist(Playlist playlist) {
		return fromPlaylistToResponsePlaylist(playlist, null);
	}
	
}
