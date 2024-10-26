package com.app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.model.enums.PlaylistType;
import com.app.model.projection.RequestPlaylist;
import com.app.model.projection.ResponseNamePlaylist;
import com.app.service.PlaylistService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;

@RestController
@RequestMapping("/api/playlists/")
@RequiredArgsConstructor
public class PlaylistController {

	private final PlaylistService playlistService;

	@GetMapping
	public Flux<ResponseNamePlaylist> getPlaylists(@RequestParam("ids") List<Integer> ids) {
		return playlistService.getPlatlistById(ids);
	}

	@GetMapping("/types")
	public Flux<PlaylistType> getPlaylistTypes() {
		return playlistService.getPlaylistTypes();
	}

	@PostMapping
	public Mono<ResponseEntity<Integer>> createPlaylist(
			@RequestPart(value = "cover", required = false) Mono<FilePart> cover, 
			@ModelAttribute Mono<RequestPlaylist> mono,
			@RequestHeader("userId") Integer userId
		) {
		return Mono.zip(mono, cover)
				.flatMap(t -> playlistService.createPlaylist(t.getT1(), t.getT2(), userId));
	}

}
