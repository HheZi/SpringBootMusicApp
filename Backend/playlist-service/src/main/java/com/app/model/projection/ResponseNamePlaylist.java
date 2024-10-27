package com.app.model.projection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ResponseNamePlaylist {

	private Integer id;
	
	private String name;
	
	private String imageUrl;
	
}
