package com.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.payload.request.CreateOrUpdatePlaylist;
import com.app.payload.response.ResponsePlaylist;
import com.app.payload.response.ResponsePlaylistPreview;
import com.app.service.PlaylistService;

import jakarta.validation.Valid;
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
	
	@GetMapping("{id}/owner")
	public Mono<Boolean> getIsCreatorOfPlaylist(
			@PathVariable("id") Integer id, 
			@RequestHeader("userId") Integer userId
		){
		return playlistService.isCreatorOfPlaylist(id, userId);
	}
	
	@GetMapping
	public Flux<ResponsePlaylistPreview> getPlaylistsByOwner(@RequestHeader("userId") Integer userId){
		return playlistService.getPlaylistsByCreatorId(userId);
	}
	
	@PostMapping
	public Mono<ResponseEntity<?>> createPlaylist(
			@Valid @ModelAttribute CreateOrUpdatePlaylist dto,
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
	
	@PutMapping("{id}")
	public Mono<Void> updatePlaylist(
			@PathVariable("id") Integer id, 
			@Valid @ModelAttribute CreateOrUpdatePlaylist dto
		){
		return playlistService.updatePlaylist(dto, id);
	}
	
	@DeleteMapping("{id}/{trackId}")
	public Mono<Void> deleteTrackFromPlaylist(
			@PathVariable("id") Integer id, 
			@PathVariable("trackId")  Long trackId
		){
		return playlistService.deleteTrackFromPlaylist(id, trackId);
	}
	
	@DeleteMapping("{id}/cover")
	public Mono<Void> deleteCover(@PathVariable("id") Integer id){
		return playlistService.deleteCover(id);
	}
	
	@DeleteMapping("{id}")
	public Mono<Void> deletePlaylist(@PathVariable("id") Integer id){
		return playlistService.deletePlaylist(id);
	}
}
