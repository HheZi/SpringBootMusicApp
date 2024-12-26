package com.app.service;

import java.io.File;
import java.nio.file.Path;
import java.util.UUID;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import com.app.kafka.message.ImageDeletionMessage;
import com.app.kafka.message.TrackDeletionMessage;
import com.app.kafka.producer.KafkaImageProducer;
import com.app.model.Playlist;
import com.app.model.PlaylistTrack;
import com.app.payload.request.CreateOrUpdatePlaylist;
import com.app.payload.response.ResponsePlaylist;
import com.app.payload.response.ResponsePlaylistPreview;
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
	
	private final KafkaImageProducer kafkaImageProducer;

	private final String TEMP_FOLDER_NAME = "temp";
	
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
			File file = new File(TEMP_FOLDER_NAME, dto.getCover().filename()).getAbsoluteFile();
			
			return dto.getCover().transferTo(file)
					.then(Mono.fromCallable(() -> playlistMapper.fromCreatePlaylistToPlaylist(dto, userId, true)))
					.flatMap(playlistRepository::save)
					.flatMap(t -> saveAuthorImage(t.getImageName(), file))
					.doFinally(t -> file.delete())
					.map(t -> ResponseEntity.status(HttpStatus.CREATED).build());
		}
		
		return Mono.just(playlistMapper.fromCreatePlaylistToPlaylist(dto, userId, false))
				.flatMap(playlistRepository::save)
				.map(t -> ResponseEntity.status(HttpStatus.CREATED).build());
	}
	
	@Transactional
	public Mono<Void> deleteCover(Integer id, Integer userId) {
		return playlistRepository.findById(id)
				.switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)))
				.filter(t -> t.getCreatedBy() == userId)
				.switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.FORBIDDEN)))
				.doOnNext(t -> {
					kafkaImageProducer.sendMessageToDeleteImage(new ImageDeletionMessage(t.getImageName()));
					t.setImageName(null);
				})
				.flatMap(playlistRepository::save)
				.then();
	}
	
	@Transactional
	public Mono<Void> deletePlaylist(Integer id, Integer userId){
		return playlistRepository.findById(id)
				.filter(t -> t.getCreatedBy() == userId)
				.switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.FORBIDDEN)))
				.doOnNext(t -> kafkaImageProducer.sendMessageToDeleteImage(new ImageDeletionMessage(t.getImageName())))
				.flatMap(playlistRepository::delete);
				
	}
	
	@Transactional
	public Mono<Void> updatePlaylist(CreateOrUpdatePlaylist dto, Integer id, Integer userId){
		Function<Playlist, Mono<Playlist>> func = playlist -> {
			playlist.setName(dto.getName());
			playlist.setDescription(dto.getDescription());
			if (playlist.getImageName() == null && dto.getCover() != null) {
				playlist.setImageName(UUID.randomUUID());
			}
			return playlistRepository.save(playlist);
		};
		
		if (dto.getCover() != null) {
			File file = new File(TEMP_FOLDER_NAME, dto.getCover().filename()).getAbsoluteFile();
			
			return dto.getCover().transferTo(file)
					.then(playlistRepository.findById(id))
					.filter(t -> t.getCreatedBy() == userId)
					.switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.FORBIDDEN)))
					.flatMap(func)
					.flatMap(t -> saveAuthorImage(t.getImageName(), file))
					.doFinally(t -> file.delete())
					.then();
					
		}
		
		return playlistRepository.findById(id)
				.filter(t -> t.getCreatedBy() == userId)
				.switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.FORBIDDEN)))
				.flatMap(func)
				.then();
	}
	
	@Transactional
	public Mono<Void> addTrackToPlaylist(Integer id, Long trackId, Integer userId){
		return playlistRepository.findById(id)
				.switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)))
				.filter(t -> t.getCreatedBy() == userId)
				.switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.FORBIDDEN)))
				.flatMap(t -> playlistTrackRepository.existsByPlaylistIdAndTrackId(id, trackId))
				.filter(t -> !t)
				.switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.CONFLICT)))
				.flatMap(t -> playlistTrackRepository.save(new PlaylistTrack(null, id, trackId)))
				.then();
	}
	
	@Transactional
	public Mono<Void> deleteTrackFromPlaylist(Integer id, Long trackId, Integer userId){
		return playlistRepository.findById(id)
				.filter(t -> t.getCreatedBy() == userId)
				.switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.FORBIDDEN)))
				.flatMap(t -> playlistTrackRepository.findByPlaylistIdAndTrackId(id, trackId))
				.switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)))
				.flatMap(playlistTrackRepository::delete);
	}
	
	public void deleteTrackFromAllPlaylists(TrackDeletionMessage trackDeletionMessage) {
		playlistTrackRepository
		.deleteByTrackId(trackDeletionMessage.trackId())
		.subscribe();
	}
 	
	private Mono<Void> saveAuthorImage(UUID name, File pathToFile) {
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
	
}
