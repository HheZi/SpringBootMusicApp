package com.app.model.projection;

import org.springframework.http.codec.multipart.FilePart;

import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;

@NoArgsConstructor
@Data
public class CreateTrackDto {

	private String title;
	
	private Long playlistId;
	
	private String author;
	
	private byte[] audio;
}
