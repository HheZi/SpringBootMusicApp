package com.app.util;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.app.model.Playlist;
import com.app.model.enums.PlaylistType;
import com.app.model.projection.RequestPlaylist;
import com.app.model.projection.ResponseNamePlaylist;

@Component
public class PlaylistMapper {

	private final String IMAGE_URL = "http://localhost:8080/api/images/";
	
	public Playlist fromRequestPlaylistToPlaylist(RequestPlaylist dto, Integer userId) {
		return Playlist.builder()
				.name(dto.getName())
				.imageName(UUID.randomUUID().toString())
				.createdBy(userId)
				.playlistType(dto.getPlaylistType())
				.build();
	}
	
	public ResponseNamePlaylist fromPlaylistToResponseNamePlaylist(Playlist playlist) {
		return ResponseNamePlaylist.builder()
				.name(playlist.getName())
				.imageUrl(IMAGE_URL + playlist.getImageName())
				.build();
	}
	
}
