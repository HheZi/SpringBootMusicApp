package com.app.payload.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestToUpdateAlbum {
	
	@NotBlank(message = "Name can't be blank")
	private String name;
	
	@PastOrPresent(message = "Album release date can't be in future")
	@NotNull(message = "Release date is required")
	private LocalDate releaseDate;
	
	@Nullable
	private FilePart cover;
	
}
