package com.app.service;

import com.app.exception.FileValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ImageValidatorService {

	@Value("${file.max-size}")
	private Integer MAX_IMAGE_SIZE;

	private final List<MediaType> ALLOWED_CONTENT_TYPES = List.of(MediaType.IMAGE_JPEG, MediaType.IMAGE_PNG);

	public Mono<Boolean> validateImageFile(FilePart value) {
		if (value == null) {
			return Mono.just(true);
		}

		MediaType contentType = value.headers().getContentType();

		if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
			return Mono.error(() -> new FileValidationException("Wrong format of file. Can be only JPEG and PNG"));
		}

		return value.content()
				.map(t -> t.readableByteCount())
				.reduce(Integer::sum)
				.map(t -> t <= MAX_IMAGE_SIZE)
				.flatMap(t -> {
					if (!t) {
						return Mono.error(() -> new FileValidationException(
								"File too large. Max size of file " + (MAX_IMAGE_SIZE / 1024 / 1024) + "MB"));
					}
					return Mono.just(true);
				});
	}

}
