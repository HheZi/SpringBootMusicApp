package com.app.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlbumPreviewResponse {

	private Integer id;
	
	private String name;
	
	private String imageUrl;
	
	private Integer authorId;
	
}
