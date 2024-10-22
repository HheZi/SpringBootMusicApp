package com.app.util;

import org.springframework.stereotype.Component;

import com.app.model.Playlist;
import com.app.model.projection.RequestPlaylist;
import com.app.model.projection.ResponseNamePlaylist;

@Component
public class PlaylistMapper {

	public Playlist fromRequestPlaylistToPlaylist(RequestPlaylist dto) {
		return Playlist.builder()
				.name(dto.getName())
				.build();
	}
	
	public ResponseNamePlaylist fromPlaylistToResponseNamePlaylist(Playlist playlist) {
		return ResponseNamePlaylist.builder()
				.name(playlist.getName())
				.build();
	}
	
}
