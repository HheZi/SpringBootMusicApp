package com.gateway.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseAlbumFromService {
	
	private Integer id;
	
	private String name;
	
	private String imageUrl;
	
	private Integer authorId;
	
	private LocalDate releaseDate;
	
	
}
