package com.app.util;

import org.springframework.stereotype.Component;

import com.app.model.Author;
import com.app.payload.response.AuthorResponse;

@Component
public class AuthorMapper {

	public AuthorResponse fromAuthorToAuthorResponse(Author author) {
		return AuthorResponse.builder()
				.id(author.getId())
				.name(author.getName())
				.build();
	}
	
}
