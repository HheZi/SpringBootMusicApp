package com.app.util;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.app.model.Author;
import com.app.payload.request.AuthorCreateOrUpdateRequest;
import com.app.payload.response.AuthorResponse;

@Component
public class AuthorMapper {

	private final String IMAGE_URL_FORMAT = "http://localhost:8080/api/images/";
	
	private final String IMAGE_URL_DEFAULT = IMAGE_URL_FORMAT + "default";
	
	public Author fromAuthorRequestToAuthor(AuthorCreateOrUpdateRequest dto, Integer userId,boolean isFilePresent) {	
		return Author.builder()
				.name(dto.getName())
				.imageName(isFilePresent ? UUID.randomUUID() : null)
				.createdBy(userId)
				.build();
	}
	
	public AuthorResponse fromAuthorToAuthorResponse(Author author, boolean includeDescription) {
		return AuthorResponse.builder()
				.id(author.getId())
				.name(author.getName())
				.description(includeDescription ? author.getDescription() : null)
				.imageUrl(author.getImageName() == null ? IMAGE_URL_DEFAULT : IMAGE_URL_FORMAT + author.getImageName())
				.build();
	}
	
}
