package com.app.service;

import java.nio.file.Path;
import java.util.UUID;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import com.app.model.Playlist;
import com.app.model.PlaylistTrack;
import com.app.payload.request.CreateOrUpdatePlaylist;
import com.app.payload.request.SavePlaylistImageRequest;
import com.app.payload.response.ResponsePlaylist;
import com.app.payload.response.ResponsePlaylistPreview;
import com.app.repository.PlaylistRepository;
import com.app.repository.PlaylistTrackRepository;
import com.app.util.PlaylistMapper;

import lombok.RequiredArgsConstructor;
import lombok.experimental.var;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PlaylistService {

	private final PlaylistRepository playlistRepository;
	
	private final PlaylistTrackRepository playlistTrackRepository;
	
	private final PlaylistMapper playlistMapper;
	
	private final WebClient.Builder builder;

	@Value("${file.temp}")
	private String tempFolder;
	
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
	
	public Flux<ResponsePlaylistPreview> getPlaylistsByCreatorId(Integer userId){
		return (playlistRepository.findByCreatedBy(userId))
				.switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)))
				.map(playlistMapper::fromPlaylistToResponsePlaylistPreview);

	}
	
	public Mono<Boolean> isCreatorOfPlaylist(Integer id, Integer userId){
		return playlistRepository.findById(id)
				.map(t -> t.getCreatedBy() == userId);
	}
	
	@Transactional
	public Mono<ResponseEntity<?>> createPlaylist(CreateOrUpdatePlaylist dto, Integer userId){
		if (dto.getCover() != null) {
			Path path = Path.of(tempFolder, dto.getCover().filename());
			
			return dto.getCover().transferTo(path)
					.then(Mono.fromCallable(() -> playlistMapper.fromCreatePlaylistToPlaylist(dto, userId, true)))
					.flatMap(playlistRepository::save)
					.flatMap(t -> saveAuthorImage(t.getImageName(), path))
					.doFinally(t -> path.toFile().delete())
					.map(t -> ResponseEntity.status(HttpStatus.CREATED).build());
		}
		
		return Mono.just(playlistMapper.fromCreatePlaylistToPlaylist(dto, userId, false))
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
		Function<Playlist, Mono<Playlist>> func = playlist -> {
			if(dto.getName() != null && !dto.getName().isEmpty() && !dto.getName().isBlank()) {
				playlist.setName(dto.getName());
			}
			if (dto.getDescription() != null) {
				playlist.setDescription(dto.getDescription());
			}
			if (playlist.getImageName() == null && dto.getCover() != null) {
				playlist.setImageName(UUID.randomUUID());
			}
			return playlistRepository.save(playlist);
		};
		
		if (dto.getCover() != null) {
			Path path = Path.of(tempFolder, dto.getCover().filename());
			
			return dto.getCover().transferTo(path)
					.then(playlistRepository.findById(id))
					.flatMap(func)
					.flatMap(t -> saveAuthorImage(t.getImageName(), path))
					.doFinally(t -> path.toFile().delete())
					.then();
					
		}
		
		return playlistRepository.findById(id)
				.flatMap(func)
				.then();
	}
	
	@Transactional
	public Mono<Void> addTrackToPlaylist(Integer id, Long trackId){
		return playlistRepository.findById(id)
				.switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)))
				.flatMap(t -> playlistTrackRepository.existsByPlaylistIdAndTrackId(id, trackId))
				.filter(t -> !t)
				.switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.CONFLICT)))
				.flatMap(t -> playlistTrackRepository.save(new PlaylistTrack(null, id, trackId)))
				.then();
	}
	
	@Transactional
	public Mono<Void> deleteTrackFromPlaylist(Integer id, Long trackId){
		return playlistTrackRepository.findByPlaylistIdAndTrackId(id, trackId)
				.switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)))
				.flatMap(playlistTrackRepository::delete);
	}
	
	private Mono<Void> saveAuthorImage(UUID name, Path pathToFile) {
		if (name == null) Mono.empty();			

		MultipartBodyBuilder multipartbuilder = new MultipartBodyBuilder();
		
		multipartbuilder.part("file", new FileSystemResource(pathToFile));
		multipartbuilder.part("name", name.toString());
		
		return builder.build()
    			.post()
    			.uri("http://image-service/api/images/")
    			.body(BodyInserters.fromMultipartData(multipartbuilder.build()))
    			.retrieve()
    			.bodyToMono(Void.class);
		
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
