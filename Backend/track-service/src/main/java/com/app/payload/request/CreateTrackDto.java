package com.app.payload.request;

import org.springframework.http.codec.multipart.FilePart;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CreateTrackDto {

	private String title;
	
	private Integer albumId;
	
	private FilePart audio;
}
