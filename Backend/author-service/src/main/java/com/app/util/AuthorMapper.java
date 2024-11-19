package com.app.util;

import java.util.UUID;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;

import com.app.model.Author;
import com.app.payload.request.AuthorCreateRequest;
import com.app.payload.response.AuthorResponse;

@Component
public class AuthorMapper {

	private final String IMAGE_URL_FORMAT = "http://localhost:8080/api/images/";
	
	private final String IMAGE_URL_DEFAULT = IMAGE_URL_FORMAT + "default";
	
	public Author fromAuthorRequestToAuthor(AuthorCreateRequest dto, boolean isFilePresent) {	
		return Author.builder()
				.name(dto.getName())
				.imageName(isFilePresent ? UUID.randomUUID() : null)
				.build();
	}
	
	public AuthorResponse fromAuthorToAuthorResponse(Author author) {
		return AuthorResponse.builder()
				.id(author.getId())
				.name(author.getName())
				.imageUrl(author.getImageName() == null ? IMAGE_URL_DEFAULT : IMAGE_URL_FORMAT + author.getImageName())
				.build();
	}
	
}
