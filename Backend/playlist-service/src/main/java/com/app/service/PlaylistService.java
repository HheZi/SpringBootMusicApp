package com.app.service;

import java.util.UUID;

import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import com.app.model.PlaylistTrack;
import com.app.payload.request.CreateOrUpdatePlaylist;
import com.app.payload.request.SavePlaylistImageRequest;
import com.app.payload.response.ResponsePlaylist;
import com.app.repository.PlaylistRepository;
import com.app.repository.PlaylistTrackRepository;
import com.app.util.PlaylistMapper;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PlaylistService {

	private final PlaylistRepository playlistRepository;
	
	private final PlaylistTrackRepository playlistTrackRepository;
	
	private final PlaylistMapper playlistMapper;
	
	private final WebClient.Builder builder;

	public Mono<ResponsePlaylist> getPlaylistById(Integer id){
		return Mono.zip(playlistRepository.findById(id), getTrackIdsByPlaylist(id).collectList())
				.switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)))
				.map(t -> playlistMapper.fromPlaylistToResponsePlaylist(t.getT1(), t.getT2()));
	}
	
	public Flux<ResponsePlaylist> findPlaylistsBySymbol(String symbol){
		return playlistRepository.findByNameStartsWithAllIgnoreCase(symbol)
				.switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)))
				.map(playlistMapper::fromPlaylistToResponsePlaylist);
	}
	
	public Flux<Long> getTrackIdsByPlaylist(Integer id){
		return playlistTrackRepository.findByPlaylistId(id)
				.map(t -> t.getTrackId());
	}
	
	public Mono<Boolean> isCreatorOfPlaylist(Integer id, Integer userId){
		return playlistRepository.findById(id)
				.map(t -> t.getCreatedBy() == userId);
	}
	
	@Transactional
	public Mono<ResponseEntity<?>> createPlaylist(CreateOrUpdatePlaylist dto, Integer userId){
		return Mono.just(
				playlistMapper.fromCreatePlaylistToPlaylist(dto, userId, dto.getCover() != null))
				.doOnNext(t -> saveAuthorImage(t.getImageName(), dto.getCover()))
				.flatMap(playlistRepository::save)
				.map(t -> ResponseEntity.status(HttpStatus.CREATED).build());
	}
	
	@Transactional
	public Mono<Void> deleteCover(Integer id) {
		return playlistRepository.findById(id)
				.doOnNext(t -> {
					deletePlalyistCover(t.getImageName());
					t.setImageName(null);
				})
				.flatMap(playlistRepository::save)
				.then();
	}
	
	@Transactional
	public Mono<Void> deletePlaylist(Integer id){
		return playlistTrackRepository.deleteByPlaylistId(id)
				.then(playlistRepository.findById(id))
				.doOnNext(t -> deletePlalyistCover(t.getImageName()))
				.flatMap(playlistRepository::delete);
				
	}
	
	@Transactional
	public Mono<Void> updatePlaylist(CreateOrUpdatePlaylist dto, Integer id){
		return playlistRepository.findById(id)
				.flatMap(t -> {
					if(dto.getName() != null) {
						t.setName(dto.getName());
					}
					if (dto.getDescription() != null) {
						t.setDescription(dto.getDescription());
					}
					if (t.getImageName() == null && dto.getCover() != null) {
						t.setImageName(UUID.randomUUID());
					}
					return playlistRepository.save(t);
				})
				.doOnNext(t -> saveAuthorImage(t.getImageName(), dto.getCover()))
				.then();
	}
	
	@Transactional
	public Mono<Void> addTrackToPlaylist(Integer id, Long trackId){
		return playlistRepository.findById(id)
				.switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)))
				.flatMap(t -> playlistTrackRepository.save(new PlaylistTrack(null, id, trackId)))
				.then();
	}
	
	private void saveAuthorImage(UUID name, FilePart filePart) {
		if (name ==  null || filePart == null) return;			
		
		DataBufferUtils.join(filePart.content())
	    .map(dataBuffer -> {
	    	byte[] bs = new byte[dataBuffer.readableByteCount()];
	    	dataBuffer.read(bs);
	    	DataBufferUtils.release(dataBuffer);
	    	return bs;
	    })
	    .flatMap(t -> {
	    	return builder.build()
	    			.post()
	    			.uri("http://image-service/api/images/")
	    			.bodyValue(new SavePlaylistImageRequest(name.toString(), t))
	    			.retrieve()
	    			.bodyToMono(Void.class);
	    }).subscribe();
		
	}
	
	private void deletePlalyistCover(UUID cover) {
		if (cover == null) return;
			
		builder
		.baseUrl("http://image-service/api/images/" + cover.toString())
		.build()
		.delete()
		.retrieve()
		.bodyToMono(Void.class)
		.subscribe();
	}
}
