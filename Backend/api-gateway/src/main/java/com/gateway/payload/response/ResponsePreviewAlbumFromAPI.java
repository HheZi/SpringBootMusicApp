package com.gateway.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponsePreviewAlbumFromAPI {
	private Integer id;

	private String name;
	
	private Integer authorId;

	private String imageUrl;
}
