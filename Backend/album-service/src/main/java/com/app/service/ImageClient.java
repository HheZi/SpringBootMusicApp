package com.app.service;

import com.app.exception.FileValidationException;
import com.app.exception.model.BadFileValidation;
import com.app.model.Album;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.File;

@Service
@RequiredArgsConstructor
public class ImageClient {

	private final WebClient.Builder builder;
	
	public Mono<Album> saveAlbumCover(Album album, File pathToFile) {
		if (album.getImageName() == null) return Mono.just(album);

		MultipartBodyBuilder multipartbuilder = new MultipartBodyBuilder();
		
		multipartbuilder.part("file", new FileSystemResource(pathToFile));
		multipartbuilder.part("name", album.getImageName().toString());
		
		return builder.build().post().uri("http://file-service/api/files/images/")
				.body(BodyInserters.fromMultipartData(multipartbuilder.build()))
				.retrieve()
				.onStatus(HttpStatusCode::is4xxClientError, this::handleBadRequestError)
				.toBodilessEntity()
				.map(voidResponseEntity -> album);

	}

	private Mono<? extends Throwable> handleBadRequestError(ClientResponse clientResponse){
		return clientResponse.bodyToMono(BadFileValidation.class)
				.flatMap(e ->  Mono.error(() -> new FileValidationException(e.getReason())));
	}
}
