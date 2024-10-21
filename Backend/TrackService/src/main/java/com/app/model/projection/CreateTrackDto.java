package com.app.model.projection;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CreateTrackDto {

	private String title;
	
	private Long playlistId;
	
	private Integer authorId;
}
