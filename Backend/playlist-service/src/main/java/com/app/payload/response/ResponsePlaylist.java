package com.app.payload.response;

import com.app.model.enums.PlaylistType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ResponsePlaylist {

	private Integer id;
	
	private String name;
	
	private String imageUrl;
	
	@JsonFormat(shape = Shape.STRING)
	private PlaylistType playlistType;
}
