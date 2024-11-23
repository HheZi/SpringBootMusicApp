package com.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.payload.request.CreatePlaylist;
import com.app.payload.response.ResponsePlaylist;
import com.app.service.PlaylistService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/playlists/")
@RequiredArgsConstructor
public class PlaylistController {

	private final PlaylistService playlistService;
	
	@GetMapping("{id}")
	public Mono<ResponsePlaylist> getPlaylist(@PathVariable("id") Integer id) {
		return playlistService.getPlaylistById(id);
	}
	
	@GetMapping("{id}/tracks")
	public Flux<Long> getTrackId(@PathVariable("id") Integer id){
		return playlistService.getTrackIdsByPlaylist(id);
	}
	
	@GetMapping("symbol/{symbol}")
	public Flux<ResponsePlaylist> getPlaylistsBySymbol(@PathVariable("symbol") String symbol){
		return playlistService.findPlaylistsBySymbol(symbol);
	}
	
	@PostMapping
	public Mono<ResponseEntity<?>> createPlaylist(
			@ModelAttribute CreatePlaylist dto,
			@RequestHeader("userId") Integer userId
		){
		return playlistService.createPlaylist(dto, userId);
	}
	
	@PatchMapping("{id}/{trackId}")
	public Mono<Void> addTrack(
			@PathVariable("id") Integer id, 
			@PathVariable("trackId")  Long trackId
		){
		return playlistService.addTrackToPlaylist(id, trackId);
	}
	
}
