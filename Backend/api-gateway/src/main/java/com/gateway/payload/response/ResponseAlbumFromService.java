package com.gateway.payload.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseAlbumFromService {
	
	private Integer id;
	
	private String name;
	
	private String imageUrl;
	
	private Integer authorId;
	
	private LocalDate releaseDate;
	
	private String albumType;
	
}
