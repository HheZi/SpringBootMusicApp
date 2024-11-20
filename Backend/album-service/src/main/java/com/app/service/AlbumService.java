package com.app.service;

import java.util.List;
import java.util.UUID;

import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import com.app.model.Album;
import com.app.model.enums.AlbumType;
import com.app.payload.request.RequestImage;
import com.app.payload.request.RequestAlbum;
import com.app.payload.request.RequestToUpdateAlbum;
import com.app.payload.response.ResponseAlbum;
import com.app.repository.AlbumRepository;
import com.app.util.AlbumMapper;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AlbumService {

	private final AlbumRepository albumRepository;

	private final AlbumMapper albumMapper;

	private final WebClient.Builder builder;

	public Mono<ResponseAlbum> getAlbumById(Integer id) {
		return albumRepository
				.findById(id)
				.switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)))
				.map(albumMapper::fromAlbumToResponseAlbum);
	}
	
	public Flux<ResponseAlbum> getAlbumByIds(List<Integer> ids){
		return albumRepository.findAllById(ids)
				.map(albumMapper::fromAlbumToResponseAlbum);
	}
	
	public Flux<ResponseAlbum> findAlbumBySymbol(String symbol){
		return albumRepository.findByNameIsStartingWithAllIgnoreCase(symbol)
				.map(albumMapper::fromAlbumToResponseAlbum);
	}

	public Mono<ResponseEntity<Integer>> createAlbum(RequestAlbum dto, Integer userId) {
		boolean coverIsPresent = dto.getCover() != null && !dto.getCover().filename().isEmpty();
		
		Album playlist = albumMapper.fromRequestAlbumToAlbum(dto, userId, coverIsPresent);

		if (coverIsPresent) 
			saveAlbumCover(playlist.getImageName(), dto.getCover());			

		return albumRepository.save(playlist)
				.map(t -> ResponseEntity.status(HttpStatus.CREATED).body(t.getId()));
	}

	private void saveAlbumCover(UUID name, FilePart filePart) {
		if (filePart == null || name == null) return;

		DataBufferUtils.join(filePart.content())
	    .map(dataBuffer -> {
	    	byte[] bs = new byte[dataBuffer.readableByteCount()];
	    	dataBuffer.read(bs);
	    	DataBufferUtils.release(dataBuffer);
	    	return bs;
	    })
	    .flatMap(t -> {
	    	 return builder.build().post().uri("http://image-service/api/images/")
	    	.bodyValue(new RequestImage(name.toString(), t))
	    	.retrieve()
	    	.bodyToMono(Void.class);
	    }).subscribe();

	}

	public Flux<AlbumType> getAlbumTypes() {
		return Flux.fromArray(AlbumType.values());
	}

	public Mono<Boolean> userIsOwnerOfAlbum(Integer playlistId, Integer userId){
		return albumRepository.findById(playlistId)
				.flatMap(t -> t.getCreatedBy() == userId ? Mono.just(true) : Mono.just(false));
	}

	public Mono<ResponseAlbum> updateAlbum(RequestToUpdateAlbum dto, Integer playlistId, Integer userId){
		return albumRepository.findById(playlistId)
				.filter(t -> t.getCreatedBy() == userId)
				.switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "You are not a creator of the album")))
				.flatMap(t -> {
					if (dto.getName() != null) {
						t.setName(dto.getName());
					}
					if (dto.getReleaseDate() != null) {
						t.setReleaseDate(dto.getReleaseDate());
					}
					if (dto.getCover() != null && t.getImageName() == null) {
						t.setImageName(UUID.randomUUID());
					}
					return albumRepository.save(t);
				})
				.doOnNext(t -> saveAlbumCover(t.getImageName(), dto.getCover()))
				.map(albumMapper::fromAlbumToResponseAlbum);
	}
	
	public Mono<Void> deleteAlbum(Integer id){
		return albumRepository.findById(id)
				.switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)))
				.doOnNext(t -> {
					deleteAlbumCover(t.getImageName());
					deleteAllTrackByAlbumId(t.getId());
				})
				.flatMap(albumRepository::delete);
	}
	
	public Mono<Void> deleteCoverById(Integer id) {
		return albumRepository.findById(id)
				.doOnNext(t -> {
					deleteAlbumCover(t.getImageName());
					t.setImageName(null);
				})
				.flatMap(albumRepository::save)
				.then();
	}
	
	private void deleteAlbumCover(UUID cover) {
		if (cover == null) return;
			
		builder
		.baseUrl("http://image-service/api/images/" + cover.toString())
		.build()
		.delete()
		.retrieve()
		.bodyToMono(Void.class)
		.subscribe();
	}
	
	private void deleteAllTrackByAlbumId(Integer id) {
		builder
		.baseUrl("http://track-service/api/tracks/?playlistId=" + id)
		.build()
		.delete()
		.retrieve()
		.bodyToMono(Void.class)
		.subscribe();
	}
}
