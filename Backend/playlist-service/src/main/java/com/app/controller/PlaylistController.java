package com.app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.model.enums.PlaylistType;
import com.app.payload.request.RequestPlaylist;
import com.app.payload.request.RequestToUpdatePlaylist;
import com.app.payload.response.ResponsePlaylist;
import com.app.service.PlaylistService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
	public Flux<ResponsePlaylist> getPlaylistsByIds(@RequestParam("ids") List<Integer> ids){
		return playlistService.getPlaylistsByIds(ids);
	}
	
	@GetMapping("/{id}")
	public Mono<ResponsePlaylist> getPlaylist(@PathVariable("id") Integer id) {
		return playlistService.getPlatlistById(id);
	}
	
	@GetMapping("/types")
	public Flux<PlaylistType> getPlaylistTypes() {
		return playlistService.getPlaylistTypes();
	}
	
	@GetMapping("/symbol/{symbol}")
	public Flux<ResponsePlaylist> getPlaylitsBySymbol(@PathVariable("symbol") String symbol){
		return playlistService.findPlaylistsBySymbol(URLDecoder.decode(symbol, Charset.defaultCharset()));
	}

	@GetMapping("/owner/{id}")
	public Mono<Boolean> getIsOwner(
			@PathVariable("id") Integer id, 
			@RequestHeader("userId") Integer userId
		){
		return playlistService.userIsOwnerOfPlaylist(id, userId);
	}
	
	@PostMapping
	public Mono<ResponseEntity<Integer>> createPlaylist(
			@ModelAttribute Mono<RequestPlaylist> mono,
			@RequestHeader("userId") Integer userId
		) {
		return mono.flatMap(t -> playlistService.createPlaylist(t, userId));
	}
	
	@PutMapping("/{id}")
	public Mono<ResponsePlaylist> updatePlaylist(
			@ModelAttribute Mono<RequestToUpdatePlaylist> dto, 
			@PathVariable("id") Integer id,
			@RequestHeader("userId") Integer userId
		){
		return dto.flatMap(t -> playlistService.updatePlaylist(t, id, userId));
	}
	
	@PatchMapping("/{id}")
	public Mono<Void> deleteCoverOfPlaylist(@PathVariable("id") Integer id){
		return playlistService.deleteCoverById(id);
	}
	
	@DeleteMapping("/{id}")
	public Mono<Void> deletePlaylist(@PathVariable("id") Integer id){
		return playlistService.deletePlaylist(id);
	}
	
	
}
