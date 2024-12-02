package com.app.payload.request;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class AuthorCreateOrUpdateRequest {

	@NotBlank(message = "Name can't be blank")
	private String name;
	
	@Nullable
	private FilePart cover;
	
}
