package com.app.payload.request;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.codec.multipart.FilePart;

import com.app.model.enums.PlaylistType;

import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;

@Data
@NoArgsConstructor
public class RequestPlaylist {

	private String name;
	
	private PlaylistType playlistType;
	
	private LocalDate releaseDate;
	
	private FilePart cover;
}
