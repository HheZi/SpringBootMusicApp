package com.app.util;

import com.app.model.Playlist;
import com.app.payload.request.CreateOrUpdatePlaylist;
import com.app.payload.response.ResponsePlaylist;
import com.app.payload.response.ResponsePlaylistPreview;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

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
	
	public ResponsePlaylistPreview fromPlaylistToResponsePlaylistPreview(Playlist playlist) {
		return new ResponsePlaylistPreview(
				playlist.getId(),
				playlist.getName(), 
				playlist.getImageName() != null ? IMAGE_URL_FORMAT + playlist.getImageName() : IMAGE_URL_DEFAULT
			);
	}
	
}
