package com.app.payload.request;

import java.time.LocalDate;

import org.springframework.http.codec.multipart.FilePart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestToUpdateAlbum {

	private String name;
	
	private LocalDate releaseDate;
	
	private FilePart cover;
	
}
