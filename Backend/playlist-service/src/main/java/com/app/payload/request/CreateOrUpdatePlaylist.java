package com.app.payload.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.codec.multipart.FilePart;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateOrUpdatePlaylist {

	@NotBlank(message = "Name can't be blank")
	private String name;
	
	@Length(min = 0, max = 70, message = "Description max 70 symbols")
	private String description;
	
	@Nullable
	private FilePart cover;
	
}
