package com.app.util;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.app.model.Album;
import com.app.model.enums.AlbumType;
import com.app.payload.request.RequestAlbum;
import com.app.payload.response.ResponseAlbum;

@Component
public class AlbumMapper {

	private final String IMAGE_URL = "http://localhost:8080/api/images/";
	
	private final String DEFAULT_IMAGE_URL = "http://localhost:8080/api/images/default";
	
	public Album fromRequestAlbumToAlbum(RequestAlbum dto, Integer userId, boolean coverIsPresent) {
		return Album.builder()
				.name(dto.getName())
				.imageName(coverIsPresent ?  UUID.randomUUID() : null)
				.createdBy(userId)
				.albumType(dto.getAlbumType())
				.releaseDate(dto.getReleaseDate())
				.build();
	}
	
	public ResponseAlbum fromAlbumToResponseAlbum(Album playlist) {
		return ResponseAlbum.builder()
				.id(playlist.getId())
				.name(playlist.getName())
				.albumType(playlist.getAlbumType())
				.releaseDate(playlist.getReleaseDate())
				.imageUrl(playlist.getImageName() != null ? IMAGE_URL + playlist.getImageName().toString() : DEFAULT_IMAGE_URL)
				.build();
	}
	
}
