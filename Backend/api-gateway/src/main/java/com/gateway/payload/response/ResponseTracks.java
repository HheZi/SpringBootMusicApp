package com.gateway.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseTracks {

	private Long id;
	
	private String title;
	
	private ResponseAlbumFromAPI album;
	
	private ResponseAuthorFromAPI author;
	
	private String audioUrl;
	
	private String duration;
	
}
