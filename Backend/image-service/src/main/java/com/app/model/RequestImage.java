package com.app.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RequestImage {
	
	private String name;
	
	private byte[] content;
	
}
