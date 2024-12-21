package com.gateway.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseTrack {

	private Long id;
	
	private String title;
	
	private ResponsePreviewAlbumFromAPI album;
	
	private ResponsePreviewAuthorFromAPI author;
	
	private String audioUrl;
	
	private String duration;
	
	private Boolean inFavorites;
	
}
