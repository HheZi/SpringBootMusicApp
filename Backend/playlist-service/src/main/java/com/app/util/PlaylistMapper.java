package com.app.util;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.app.model.Playlist;
import com.app.model.enums.PlaylistType;
import com.app.payload.request.RequestPlaylist;
import com.app.payload.response.ResponsePlaylist;

@Component
public class PlaylistMapper {

	private final String IMAGE_URL = "http://localhost:8080/api/images/";
	
	private final String DEFAULT_IMAGE_URL = "http://localhost:8080/api/images/default";
	
	public Playlist fromRequestPlaylistToPlaylist(RequestPlaylist dto, Integer userId, boolean coverIsPresent) {
		return Playlist.builder()
				.name(dto.getName())
				.imageName(coverIsPresent ?  UUID.randomUUID() : null)
				.createdBy(userId)
				.playlistType(dto.getPlaylistType())
				.releaseDate(dto.getReleaseDate())
				.build();
	}
	
	public ResponsePlaylist fromPlaylistToResponsePlaylist(Playlist playlist) {
		return ResponsePlaylist.builder()
				.id(playlist.getId())
				.name(playlist.getName())
				.playlistType(playlist.getPlaylistType())
				.releaseDate(playlist.getReleaseDate())
				.imageUrl(playlist.getImageName() != null ? IMAGE_URL + playlist.getImageName().toString() : DEFAULT_IMAGE_URL)
				.build();
	}
	
}
