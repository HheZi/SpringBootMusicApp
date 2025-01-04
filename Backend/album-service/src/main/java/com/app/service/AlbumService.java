package com.app.service;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import com.app.kafka.KafkaAlbumProducer;
import com.app.kafka.KafkaImageProducer;
import com.app.kafka.message.AlbumDeletionMessage;
import com.app.kafka.message.ImageDeletionMessage;
import com.app.model.Album;
import com.app.payload.request.RequestAlbum;
import com.app.payload.request.RequestToUpdateAlbum;
import com.app.payload.response.AlbumPreviewResponse;
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
	
	private final KafkaAlbumProducer kafkaAlbumProducer;
	
	private final KafkaImageProducer kafkaImageProducer;
	
	private final String TEMP_FOLDER_NAME = "temp";

	public Mono<ResponseAlbum> getAlbumById(Integer id) {
		return albumRepository
				.findById(id)
				.switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)))
				.map(albumMapper::fromAlbumToResponseAlbum);
	}
	
	public Flux<AlbumPreviewResponse> getAlbumByIds(List<Integer> ids, List<Integer> authorId){
		if (authorId != null) {
			return albumRepository.findByAuthorIdIn(authorId)
			.map(albumMapper::fromAlbumToAlbumPreviewResponse);
		}
		return albumRepository.findAllById(ids)
				.map(albumMapper::fromAlbumToAlbumPreviewResponse);
	}
	
	public Flux<AlbumPreviewResponse> findAlbumBySymbol(String symbol){
		return albumRepository.findByNameStartingWithAllIgnoreCase(symbol)
				.switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)))
				.map(albumMapper::fromAlbumToAlbumPreviewResponse);
	}

	public Mono<ResponseEntity<Integer>> createAlbum(RequestAlbum dto, Integer userId) {
		if (dto.getCover() != null) {
			File file = new File(TEMP_FOLDER_NAME, dto.getCover().filename()).getAbsoluteFile();
			
			
			
			return dto.getCover().transferTo(file)
					.then(Mono.fromCallable(() -> albumMapper.fromRequestAlbumToAlbum(dto, userId, true)))
					.flatMap(albumRepository::save)
					.flatMap(t -> saveAlbumCover(t.getImageName(), file))
					.doFinally(t -> file.delete())
					.map(t -> ResponseEntity.status(HttpStatus.CREATED).build());
		}
		
		return  Mono.just(albumMapper.fromRequestAlbumToAlbum(dto, userId, false))
				.flatMap(albumRepository::save)
				.map(t -> ResponseEntity.status(HttpStatus.CREATED).body(t.getId()));
	}

	private Mono<Void> saveAlbumCover(UUID name, File pathToFile) {
		if (name == null) return Mono.empty();

		MultipartBodyBuilder multipartbuilder = new MultipartBodyBuilder();
		
		multipartbuilder.part("file", new FileSystemResource(pathToFile));
		multipartbuilder.part("name", name.toString());
		
		return builder.build().post().uri("http://image-service/api/images/")
				.body(BodyInserters.fromMultipartData(multipartbuilder.build()))
				.retrieve()
				.bodyToMono(Void.class);

	}


	public Mono<Boolean> userIsOwnerOfAlbum(Integer playlistId, Integer userId){
		return albumRepository.findById(playlistId)
				.switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)))
				.flatMap(t -> t.getCreatedBy() == userId ? Mono.just(true) : Mono.just(false));
	}

	public Mono<Void> updateAlbum(RequestToUpdateAlbum dto, Integer playlistId, Integer userId){
		Function<Album, Mono<Album>> function = t -> {
			t.setName(dto.getName());
			t.setReleaseDate(dto.getReleaseDate());
			
			if (dto.getCover() != null && t.getImageName() == null) {
				t.setImageName(UUID.randomUUID());
			}
			return albumRepository.save(t);
		};
		
		if(dto.getCover() != null) {
			File file = new File(TEMP_FOLDER_NAME, dto.getCover().filename()).getAbsoluteFile();
			
			return dto.getCover().transferTo(file)
					.then(albumRepository.findById(playlistId))
					.filter(t -> t.getCreatedBy() == userId)
					.switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.FORBIDDEN)))
					.flatMap(function)
					.flatMap(t -> saveAlbumCover(t.getImageName(), file))
					.doFinally(t -> file.delete())
					.then();
		}
		
		return albumRepository.findById(playlistId)
				.filter(t -> t.getCreatedBy() == userId)
				.switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.FORBIDDEN)))
				.flatMap(function)
				.then();
	}
	
	public Mono<Void> deleteAlbum(Integer id, Integer userId){
		return albumRepository
				.findById(id)
				.filter(t -> t.getCreatedBy() == userId)
				.switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)))
				.doOnNext(t -> {
					kafkaAlbumProducer.sendDeleteAlbumMessage(new AlbumDeletionMessage(t.getId()));
					kafkaImageProducer.sendMessageToDeleteImage(new ImageDeletionMessage(t.getImageName()));
				})
				.flatMap(albumRepository::delete);
	}
	
	public Mono<Void> deleteCoverById(Integer id, Integer userId) {
		return albumRepository.findById(id)
				.filter(t -> t.getCreatedBy() == userId)
				.switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.FORBIDDEN)))
				.doOnNext(t -> {
					kafkaImageProducer.sendMessageToDeleteImage(new ImageDeletionMessage(t.getImageName()));
					t.setImageName(null);
				})
				.flatMap(albumRepository::save)
				.then();
	}
	
}
