package com.app.payload.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;

@NoArgsConstructor
@Data
public class CreateTrackDto {

	@NotBlank(message = "Title can't be blank")
	private String title;
	
	@NotNull(message = "Album is required")
	private Integer albumId;
	
	@Nullable
	private FilePart audio;
}
