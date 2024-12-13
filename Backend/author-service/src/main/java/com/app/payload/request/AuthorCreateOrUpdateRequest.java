package com.app.payload.request;

import org.hibernate.validator.constraints.Length;
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
	@Length(max = 75, message = "Name allows only 75 symbols")
	private String name;
	
	@NotBlank(message = "Description can't be blank")
	@Length(max = 360, message = "Description allows only 360 symbols")
	private String description;
	
	@Nullable
	private FilePart cover;
	
}
