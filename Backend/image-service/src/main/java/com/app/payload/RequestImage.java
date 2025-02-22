package com.app.payload;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;

@NoArgsConstructor
@Data
public class RequestImage {
	
	private String name;
	
	private FilePart file;
	
}
