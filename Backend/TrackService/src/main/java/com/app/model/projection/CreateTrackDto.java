package com.app.model.projection;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CreateTrackDto {

	private String title;
	
	private MultipartFile file;
	
	private Long playlistId;
	
}
