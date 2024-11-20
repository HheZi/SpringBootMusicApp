package com.app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.model.enums.AlbumType;
import com.app.payload.request.RequestAlbum;
import com.app.payload.request.RequestToUpdateAlbum;
import com.app.payload.response.ResponseAlbum;
import com.app.service.AlbumService;

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
@RequestMapping("/api/albums/")
@RequiredArgsConstructor
public class AlbumController {

	private final AlbumService albumService;

	@GetMapping
	public Flux<ResponseAlbum> getAlbumByIds(@RequestParam("ids") List<Integer> ids){
		return albumService.getAlbumByIds(ids);
	}
	
	@GetMapping("/{id}")
	public Mono<ResponseAlbum> getAlbum(@PathVariable("id") Integer id) {
		return albumService.getAlbumById(id);
	}
	
	@GetMapping("/types")
	public Flux<AlbumType> getAlbumTypes() {
		return albumService.getAlbumTypes();
	}
	
	@GetMapping("/symbol/{symbol}")
	public Flux<ResponseAlbum> getAlbumBySymbol(@PathVariable("symbol") String symbol){
		return albumService.findAlbumBySymbol(URLDecoder.decode(symbol, Charset.defaultCharset()));
	}

	@GetMapping("/owner/{id}")
	public Mono<Boolean> getIsOwner(
			@PathVariable("id") Integer id, 
			@RequestHeader("userId") Integer userId
		){
		return albumService.userIsOwnerOfAlbum(id, userId);
	}
	
	@PostMapping
	public Mono<ResponseEntity<Integer>> createAlbum(
			@ModelAttribute Mono<RequestAlbum> mono,
			@RequestHeader("userId") Integer userId
		) {
		return mono.flatMap(t -> albumService.createAlbum(t, userId));
	}
	
	@PutMapping("/{id}")
	public Mono<ResponseAlbum> updateAlbum(
			@ModelAttribute Mono<RequestToUpdateAlbum> dto, 
			@PathVariable("id") Integer id,
			@RequestHeader("userId") Integer userId
		){
		return dto.flatMap(t -> albumService.updateAlbum(t, id, userId));
	}
	
	@PatchMapping("/{id}")
	public Mono<Void> deleteCoverOfAlbum(@PathVariable("id") Integer id){
		return albumService.deleteCoverById(id);
	}
	
	@DeleteMapping("/{id}")
	public Mono<Void> deleteAlbum(@PathVariable("id") Integer id){
		return albumService.deleteAlbum(id);
	}
	
	
}
