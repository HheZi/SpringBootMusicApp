package com.app.validation;

import com.app.exception.FileValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Service
public class AudioValidatorService {

	@Value("${file.max-size}")
	private Integer MAX_AUDIO_SIZE;

	private final List<String> ALLOWED_CONTENT_TYPES = Collections.singletonList("audio/mpeg");

	public Mono<FilePart> validateAudioFile(FilePart value) {
		if (value == null) {
			return Mono.error(() -> new FileValidationException("Audio file is not specified."));
		}

		String contentType = value.headers().getContentType().toString();

		if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
			return Mono.error(() -> new FileValidationException("Wrong format of file. Can be only MP3"));
		}

		return value.content()
				.map(DataBuffer::readableByteCount)
				.reduce(Integer::sum)
				.flatMap(t -> {
					if (t >= MAX_AUDIO_SIZE) {
						return Mono.error(() -> new FileValidationException(
								"File too large. Max size of file " + (MAX_AUDIO_SIZE / 1024 / 1024) + "MB"));
					}
					return Mono.just(value);
				});
	}

}
