package com.gateway.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseTrackFromAPI {
	private Long id;
	
	private String title;
	
	private Integer playlistId;
	
	private String audioUrl;
	
	private Integer authorId;
}
