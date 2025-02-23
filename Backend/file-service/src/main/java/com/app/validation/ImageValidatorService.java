package com.app.validation;

import com.app.exception.FileValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ImageValidatorService {

	@Value("${image.max-size}")
	private Integer MAX_IMAGE_SIZE;

	private final List<MediaType> ALLOWED_CONTENT_TYPES = List.of(MediaType.IMAGE_JPEG, MediaType.IMAGE_PNG);

	public Mono<FilePart> validateImageFile(FilePart value) {
		if (value == null) {
			return Mono.error(() -> new FileValidationException("Image file is not specified"));
		}

		MediaType contentType = value.headers().getContentType();

		if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
			return Mono.error(() -> new FileValidationException("Wrong format of file. Can be only JPEG and PNG"));
		}

		return value.content()
				.map(DataBuffer::readableByteCount)
				.reduce(Integer::sum)
				.flatMap(t -> {
					if (t >= MAX_IMAGE_SIZE) {
						return Mono.error(() -> new FileValidationException(
								"File too large. Max size of file " + (MAX_IMAGE_SIZE / 1024 / 1024) + "MB"));
					}
					return Mono.just(value);
				});
	}

}
