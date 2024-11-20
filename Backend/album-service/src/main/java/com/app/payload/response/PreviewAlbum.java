package com.app.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PreviewAlbum {

	private Integer id;
	
	private String name;
	
	private String imageUrl;
	
}
