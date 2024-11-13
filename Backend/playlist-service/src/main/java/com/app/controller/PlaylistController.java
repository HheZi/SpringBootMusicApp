package com.app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.model.enums.PlaylistType;
import com.app.payload.request.RequestPlaylist;
import com.app.payload.response.ResponseNamePlaylist;
import com.app.service.PlaylistService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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

	@GetMapping("/{id}")
	public Mono<ResponseNamePlaylist> getPlaylist(@PathVariable("id") Integer id) {
		return playlistService.getPlatlistById(id);
	}
	
	@GetMapping
	public Flux<ResponseNamePlaylist> getPlaylistsByIds(@RequestParam("id[]") List<Integer> ids){
		return playlistService.getPlaylistsByIds(ids);
	}
	
	@GetMapping("/types")
	public Flux<PlaylistType> getPlaylistTypes() {
		return playlistService.getPlaylistTypes();
	}
	
	@GetMapping("/symbol/{symbol}")
	public Flux<ResponseNamePlaylist> getPlaylitsBySymbol(@PathVariable("symbol") String symbol){
		return playlistService.findPlaylistsBySymbol(URLDecoder.decode(symbol, Charset.defaultCharset()));
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
