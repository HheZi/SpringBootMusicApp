package com.app.util;

import com.app.model.Album;
import com.app.payload.request.RequestAlbum;
import com.app.payload.response.AlbumPreviewResponse;
import com.app.payload.response.ResponseAlbum;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AlbumMapper {

	private final String IMAGE_URL = "http://localhost:8080/api/images/";
	
	private final String DEFAULT_IMAGE_URL = "http://localhost:8080/api/images/default";
	
	public Album fromRequestAlbumToAlbum(RequestAlbum dto, Integer userId, boolean coverIsPresent) {
		return Album.builder()
				.name(dto.getName())
				.imageName(coverIsPresent ?  UUID.randomUUID() : null)
				.createdBy(userId)
				.authorId(dto.getAuthorId())
				.releaseDate(dto.getReleaseDate())
				.build();
	}
	
	public ResponseAlbum fromAlbumToResponseAlbum(Album album) {
		return ResponseAlbum.builder()
				.id(album.getId())
				.name(album.getName())
				.releaseDate(album.getReleaseDate())
				.authorId(album.getAuthorId())
				.imageUrl(album.getImageName() != null ? IMAGE_URL + album.getImageName().toString() : DEFAULT_IMAGE_URL)
				.build();
	}
	
	public AlbumPreviewResponse fromAlbumToAlbumPreviewResponse(Album album) {
		return AlbumPreviewResponse.builder()
				.id(album.getId())
				.authorId(album.getAuthorId())
				.imageUrl(album.getImageName() != null ? IMAGE_URL + album.getImageName().toString() : DEFAULT_IMAGE_URL)
				.name(album.getName())
				.build();
	}
	
}
