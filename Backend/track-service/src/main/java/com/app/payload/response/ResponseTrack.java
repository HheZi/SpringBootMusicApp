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
	
	private Integer albumId;
	
	private String audioUrl;
	
	private String duration;
}
