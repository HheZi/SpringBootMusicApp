package com.app.service;

import com.app.enums.UserRole;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class AlbumService {

	private final AlbumRepository albumRepository;

	private final AlbumMapper albumMapper;

	private final KafkaAlbumProducer kafkaAlbumProducer;
	
	private final KafkaImageProducer kafkaImageProducer;
	
	private final ImageClient imageClient;
	
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

	public Mono<ResponseEntity<?>> createAlbum(RequestAlbum dto, UserRole userRole) {
		if (userRole != UserRole.ADMIN) {
			return Mono.error(() -> new ResponseStatusException(HttpStatus.FORBIDDEN));
		}

		if (dto.getCover() != null) {
			File file = new File(TEMP_FOLDER_NAME, dto.getCover().filename()).getAbsoluteFile();
			
			return dto.getCover().transferTo(file)
					.then(Mono.fromCallable(() -> albumMapper.fromRequestAlbumToAlbum(dto, true)))
					.flatMap(t -> imageClient.saveAlbumCover(t, file))
					.flatMap(albumRepository::save)
					.doFinally(t -> file.delete())
					.map(t -> ResponseEntity.status(HttpStatus.CREATED).build());
		}
		
		return  Mono.just(albumMapper.fromRequestAlbumToAlbum(dto, false))
				.flatMap(albumRepository::save)
				.map(t -> ResponseEntity.status(HttpStatus.CREATED).build());
	}

	public Mono<Void> updateAlbum(RequestToUpdateAlbum dto, Integer albumId, UserRole userRole) {
		if (userRole != UserRole.ADMIN) {
			return Mono.error(() -> new ResponseStatusException(HttpStatus.FORBIDDEN));
		}

		if(dto.getCover() != null) {
			File file = new File(TEMP_FOLDER_NAME, dto.getCover().filename()).getAbsoluteFile();
			
			return dto.getCover().transferTo(file)
					.then(albumRepository.findById(albumId))
					.switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)))
					.flatMap(t -> mapAlbumForUpdate(dto, t))
					.flatMap(t ->  imageClient.saveAlbumCover(t, file))
					.flatMap(albumRepository::save)
					.doFinally(t -> file.delete())
					.then();
		}
		
		return albumRepository.findById(albumId)
				.switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)))
				.flatMap(t -> mapAlbumForUpdate(dto, t))
				.flatMap(albumRepository::save)
				.then();
	}
	
	private Mono<Album> mapAlbumForUpdate(RequestToUpdateAlbum dto, Album album){
		album.setName(dto.getName());
		album.setReleaseDate(dto.getReleaseDate());

		if (dto.getCover() != null && album.getImageName() == null) {
			album.setImageName(UUID.randomUUID());
		}
		return Mono.just(album);
	}
	
	public Mono<Void> deleteAlbum(Integer id, UserRole userRole){
		if (userRole != UserRole.ADMIN) {
			return Mono.error(() -> new ResponseStatusException(HttpStatus.FORBIDDEN));
		}

		return albumRepository
				.findById(id)
				.switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)))
				.doOnNext(t -> {
					kafkaAlbumProducer.sendDeleteAlbumMessage(new AlbumDeletionMessage(t.getId()));
					if (nonNull(t.getImageName())){
						kafkaImageProducer.sendMessageToDeleteImage(new ImageDeletionMessage(t.getImageName().toString()));
					}
				})
				.flatMap(albumRepository::delete);
	}
	
	public Mono<Void> deleteCoverById(Integer id, UserRole userRole) {
		if (userRole != UserRole.ADMIN) {
			return Mono.error(() -> new ResponseStatusException(HttpStatus.FORBIDDEN));
		}

		return albumRepository.findById(id)
				.switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)))
				.doOnNext(t -> {
					if (nonNull(t.getImageName())){
						kafkaImageProducer.sendMessageToDeleteImage(new ImageDeletionMessage(t.getImageName().toString()));
					}
					t.setImageName(null);
				})
				.flatMap(albumRepository::save)
				.then();
	}
	
}
