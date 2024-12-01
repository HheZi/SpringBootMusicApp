package com.app.validation.validator;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;

import com.app.validation.AudioValid;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AudioValidator implements ConstraintValidator<AudioValid, FilePart> {

	@Value("${file.max-size}")
	private Integer MAX_AUDIO_SIZE; 
	
	private final List<String> ALLOWED_CONTENT_TYPES = List.of("audio/mpeg");
	
	@Override
	public boolean isValid(FilePart value, ConstraintValidatorContext context) {
		if (value == null) {
			return true;
		}
		
		String contentType = value.headers().getContentType().toString();
		
		if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
			context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Wrong format of file. Can be only MP3")
                    .addConstraintViolation();
			return false;
		}
		
		Boolean block = value.content().map(t -> t.readableByteCount())
		.reduce((t, u) -> t+u)
		.map(t -> t <= MAX_AUDIO_SIZE )
		.block();
		
		if (!block) {
			context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Max size of file " + (MAX_AUDIO_SIZE / 1024 / 1024) + "MB")
                    .addConstraintViolation();
			return false;
		}
		
		return true;
	}

}
