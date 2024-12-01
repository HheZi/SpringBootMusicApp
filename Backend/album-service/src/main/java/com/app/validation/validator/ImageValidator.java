package com.app.validation.validator;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;

import com.app.validation.ImageValid;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ImageValidator implements ConstraintValidator<ImageValid, FilePart> {

	@Value("${file.max-size}")
	private Integer MAX_IMAGE_SIZE; 
	
	private List<MediaType> ALLOWED_CONTENT_TYPES = List.of(MediaType.IMAGE_JPEG, MediaType.IMAGE_PNG);
	
	@Override
	public boolean isValid(FilePart value, ConstraintValidatorContext context) {
		if (value == null) {
			return true;
		}
		
		MediaType contentType = value.headers().getContentType();
		
		if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
			context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Wrong format of file. Can be only JPEG and PNG")
                    .addConstraintViolation();
			return false;
		}
		
		Boolean block = value.content().map(t -> t.readableByteCount())
		.reduce((t, u) -> t+u)
		.map(t -> t <= MAX_IMAGE_SIZE )
		.block();
		if (!block) {
			context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Max size of file " + (MAX_IMAGE_SIZE / 1024 / 1024) + "MB")
                    .addConstraintViolation();
			return false;
		}
		
		
		return true;
	}

}
