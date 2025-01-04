package com.app.payload;

import org.springframework.http.codec.multipart.FilePart;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RequestImage {
	
	private String name;
	
	private FilePart file;
	
}
