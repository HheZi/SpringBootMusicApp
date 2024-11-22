package com.app.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponsePlaylist {

	private Integer id;
	
	private String name;
	
	private String description;
	
	private String imageUrl;
	
}
