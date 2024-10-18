package com.app.model.projection;

import org.springframework.http.codec.multipart.FilePart;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CreateTrackDto {

	private String title;
	
	private FilePart file;
	
	private Long playlistId;
	
}
