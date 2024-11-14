package com.app.payload.request;

import org.springframework.http.codec.multipart.FilePart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestToUpdatePlaylist {

	private String name;
	
	private FilePart cover;
	
}
