package com.app.payload.request;

import java.time.LocalDate;

import org.springframework.http.codec.multipart.FilePart;

import com.app.validation.ImageValid;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestToUpdateAlbum {
	
	@NotBlank(message = "Name can't be blank")
	private String name;
	
	@PastOrPresent(message = "Album release date can't be in future")
	private LocalDate releaseDate;
	
	@ImageValid
	private FilePart cover;
	
}
