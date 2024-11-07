package com.app.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class ResponseTrack {
	
	private Long id;
	
	private String title;
	
	private Long playlistId;
	
	private String audioUrl;
	
	private Integer authorId;
}
