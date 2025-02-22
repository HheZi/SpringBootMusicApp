package com.app.service;

import com.app.exception.FileValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class AudioValidatorService {

	@Value("${file.max-size}")
	private Integer MAX_AUDIO_SIZE;

	private final List<String> ALLOWED_CONTENT_TYPES = List.of("audio/mpeg");

	public Mono<Boolean> validateAudioFile(FilePart value) {
		if (value == null) {
			return Mono.just(true);
		}

		String contentType = value.headers().getContentType().toString();

		if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
			return Mono.error(() -> new FileValidationException("Wrong format of file. Can be only MP3"));
		}

		return value.content()
				.map(t -> t.readableByteCount())
				.reduce(Integer::sum)
				.map(t -> t <= MAX_AUDIO_SIZE)
				.flatMap(t -> {
					if (!t) {
						return Mono.error(() -> new FileValidationException(
								"File too large. Max size of file " + (MAX_AUDIO_SIZE / 1024 / 1024) + "MB"));
					}
					return Mono.just(true);
				});
	}

}
