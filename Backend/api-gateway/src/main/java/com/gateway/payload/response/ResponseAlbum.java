package com.gateway.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseAlbum {

	private Integer id;
	
	private String name;
	
	private String imageUrl;
	
	private LocalDate releaseDate;
	
	private Integer numberOfTracks;
	
	private String totalDuration;
	
	private ResponseAuthorFromService author;
	
}
