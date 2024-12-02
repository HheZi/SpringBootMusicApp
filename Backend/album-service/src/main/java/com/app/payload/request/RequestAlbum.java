package com.app.payload.request;

import java.time.LocalDate;

import org.springframework.http.codec.multipart.FilePart;

import com.app.model.enums.AlbumType;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RequestAlbum {

	@NotBlank(message = "Name can't be blank")
	private String name;
	
	@NotNull(message = "Album type required")
	private AlbumType albumType;
	
	@PastOrPresent(message = "Album release date can't be in future")
	@NotNull
	private LocalDate releaseDate;
	
	@NotNull(message = "Author is required")
	private Integer authorId;
	
	@Nullable
	private FilePart cover;
}
