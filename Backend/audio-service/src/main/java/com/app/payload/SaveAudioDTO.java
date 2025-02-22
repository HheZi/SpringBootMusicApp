package com.app.payload;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;

@Data
@NoArgsConstructor
public class SaveAudioDTO {
	
	private String name;
	
	private FilePart file;
}
