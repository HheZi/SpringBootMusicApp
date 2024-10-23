package com.app.util;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.app.model.Playlist;
import com.app.model.enums.PlaylistType;
import com.app.model.projection.RequestPlaylist;
import com.app.model.projection.ResponseNamePlaylist;

@Component
public class PlaylistMapper {

	public Playlist fromRequestPlaylistToPlaylist(RequestPlaylist dto, Integer userId, PlaylistType playlistType) {
		return Playlist.builder()
				.name(dto.getName())
				.imageName(UUID.randomUUID().toString())
				.playlistType(playlistType)
				.createdBy(userId)
				.build();
	}
	
	public ResponseNamePlaylist fromPlaylistToResponseNamePlaylist(Playlist playlist) {
		return ResponseNamePlaylist.builder()
				.name(playlist.getName())
				.build();
	}
	
}
