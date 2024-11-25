package com.app.payload.request;

import org.springframework.http.codec.multipart.FilePart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AuthorCreateOrUpdateRequest {

	private String name;
	
	private FilePart cover;
	
}
