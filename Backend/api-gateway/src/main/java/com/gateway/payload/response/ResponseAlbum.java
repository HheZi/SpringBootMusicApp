package com.gateway.payload.response;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseAlbum {

	private Integer id;
	
	private String name;
	
	private String imageUrl;
	
	private LocalDate releaseDate;
	
	private String albumType;
	
	private Integer numberOfTracks;
	
	private String totalDuration;
	
	private ResponseAuthorFromService author;
	
}
