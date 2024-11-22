package com.app.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveAuthorImageRequest {
	
	private String name;
	
	private byte[] content;
	
}
