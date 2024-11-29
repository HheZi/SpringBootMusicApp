package com.app.payload.request;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.codec.multipart.FilePart;

import com.app.model.enums.AlbumType;

import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;

@Data
@NoArgsConstructor
public class RequestAlbum {

	private String name;
	
	private AlbumType albumType;
	
	private LocalDate releaseDate;
	
	private Integer authorId;
	
	private FilePart cover;
}
