package com.app.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponsePlaylist {

	private Integer id;
	
	private String name;
	
	private String description;
	
	private String imageUrl;
	
	@JsonInclude(value = Include.NON_NULL)
	private List<Long> trackIds;
	
	@JsonInclude(value = Include.NON_NULL)
	private Integer numberOfTracks;
}
