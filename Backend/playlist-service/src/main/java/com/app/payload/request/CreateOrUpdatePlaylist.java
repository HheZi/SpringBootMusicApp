package com.app.payload.request;

import org.springframework.http.codec.multipart.FilePart;

import com.app.validation.ImageValid;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateOrUpdatePlaylist {

	@NotBlank(message = "Name can't be blank")
	private String name;
	
	@Nullable
	private String description;
	
	@ImageValid
	private FilePart cover;
	
}
