package com.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.payload.request.CreateOrUpdatePlaylist;
import com.app.payload.response.ResponsePlaylist;
import com.app.payload.response.ResponsePlaylistPreview;
import com.app.service.ImageValidatorService;
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
	
	private final ImageValidatorService imageValidatorService;
	
	@GetMapping("{id}")
	public Mono<ResponsePlaylist> getPlaylist(@PathVariable("id") Integer id) {
		return playlistService.getPlaylistById(id);
	}
	
	@GetMapping("tracks/{id}")
	public Flux<Long> getTrackId(@PathVariable("id") Integer id){
		return playlistService.getTrackIdsByPlaylist(id);
	}
	
	@GetMapping("symbol/{symbol}")
	public Flux<ResponsePlaylist> getPlaylistsBySymbol(@PathVariable("symbol") String symbol){
		return playlistService.findPlaylistsBySymbol(symbol);
	}
	
	@GetMapping("owner/{id}")
	public Mono<Boolean> getIsCreatorOfPlaylist(
			@PathVariable("id") Integer id, 
			@RequestHeader(value = "userId",
			required = false, 
			defaultValue = "0") Integer userId
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
		return imageValidatorService.validateImageFile(dto.getCover())
				.flatMap(t -> playlistService.createPlaylist(dto, userId));
	}
	
	@PatchMapping("{id}/{trackId}")
	public Mono<Void> addTrack(
			@PathVariable("id") Integer id, 
			@PathVariable("trackId")  Long trackId,
			@RequestHeader("userId") Integer userId
		){
		return playlistService.addTrackToPlaylist(id, trackId, userId);
	}
	
	@PutMapping("{id}")
	public Mono<Void> updatePlaylist(
			@Valid @ModelAttribute CreateOrUpdatePlaylist dto,
			@PathVariable("id") Integer id,
			@RequestHeader("userId") Integer userId
		){
		return imageValidatorService.validateImageFile(dto.getCover())
				.flatMap(t -> playlistService.updatePlaylist(dto, id, userId));
	}
	
	@DeleteMapping("{id}/{trackId}")
	public Mono<Void> deleteTrackFromPlaylist(
			@PathVariable("id") Integer id, 
			@PathVariable("trackId")  Long trackId,
			@RequestHeader("userId") Integer userId
		){
		return playlistService.deleteTrackFromPlaylist(id, trackId, userId);
	}
	
	@DeleteMapping("cover/{id}")
	public Mono<Void> deleteCover(
			@PathVariable("id") Integer id,
			@RequestHeader("userId") Integer userId
		){
		return playlistService.deleteCover(id, userId);
	}
	
	@DeleteMapping("{id}")
	public Mono<Void> deletePlaylist(
			@PathVariable("id") Integer id,
			@RequestHeader("userId") Integer userId
		){
		return playlistService.deletePlaylist(id, userId);
	}
}
