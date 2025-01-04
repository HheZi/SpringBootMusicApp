package com.app.payload;

import org.springframework.http.codec.multipart.FilePart;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SaveAudioDTO {
	
	private String name;
	
	private FilePart file;
}
