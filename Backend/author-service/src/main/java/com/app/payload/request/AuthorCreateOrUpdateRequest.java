package com.app.payload.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.codec.multipart.FilePart;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AuthorCreateOrUpdateRequest {

	@NotBlank(message = "Name can't be blank")
	@Length(max = 75, message = "Name allows only 75 symbols")
	private String name;
	
	@Length(max = 360, message = "Description allows only 360 symbols")
	private String description;
	
	@Nullable
	private FilePart cover;
	
}
