package com.app.service;

import java.nio.file.Path;
import java.util.List;
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

import com.app.kafka.KafkaImageProducer;
import com.app.kafka.message.ImageDeletionMessage;
import com.app.model.Author;
import com.app.payload.request.AuthorCreateOrUpdateRequest;
import com.app.payload.response.AuthorResponse;
import com.app.repository.AuthorRepository;
import com.app.util.AuthorMapper;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthorService {

	private final AuthorRepository authorRepository;

	private final AuthorMapper authorMapper;
	
	private final WebClient.Builder builder;
	
	private final KafkaImageProducer kafkaImageProducer;
	
	@Value("${file.temp}")
	private String tempFolder;
	
	public Mono<AuthorResponse> getAuthorById(Integer id) {
		return authorRepository
				.findById(id)
				.switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)))
				.map(a -> authorMapper.fromAuthorToAuthorResponse(a, true));
	}
	
	public Flux<AuthorResponse> getAuthorsByIds(List<Integer> ids){
		return authorRepository.findAllById(ids)
				.map(a -> authorMapper.fromAuthorToAuthorResponse(a, false));
	}
	
	public Flux<AuthorResponse> getAuthorByFirstSymbols(String symbols){
		return authorRepository
				.findByNameStartingWithIgnoreCase(symbols)
				.switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)))
				.map(a -> authorMapper.fromAuthorToAuthorResponse(a, false));
	}

	@Transactional
	public Mono<ResponseEntity<?>> saveAuthor(AuthorCreateOrUpdateRequest dto, Integer userId) {
		if (dto.getCover() != null) {
			Path path = Path.of(tempFolder, dto.getCover().filename());
			
			return dto.getCover().transferTo(path)
					.then(Mono.fromCallable(() -> authorMapper.fromAuthorRequestToAuthor(dto, userId, true)))
					.flatMap(authorRepository::save)
					.flatMap(t -> saveAuthorImage(t.getImageName(), path))
					.doFinally(t -> path.toFile().delete())
					.map(t -> ResponseEntity.status(HttpStatus.CREATED).build());
		}
		
		return  Mono.just(authorMapper.fromAuthorRequestToAuthor(dto, userId, false))
				.flatMap(authorRepository::save)
				.map(t -> ResponseEntity.status(HttpStatus.CREATED).build());
	}
	
	@Transactional
	public Mono<Void> updateAuthor(AuthorCreateOrUpdateRequest dto, Integer id, Integer userId){
		Function<Author, Mono<Author>> function = t -> {
				t.setName(dto.getName());
				t.setDescription(dto.getDescription());
			if (t.getImageName() == null && dto.getCover() != null) {
				t.setImageName(UUID.randomUUID());
			}
			return authorRepository.save(t);
		};
		
		if(dto.getCover() != null) {
			Path path = Path.of(tempFolder, dto.getCover().filename());
			
			return dto.getCover().transferTo(path)
					.then(authorRepository.findById(id))
					.filter(t -> t.getCreatedBy() == userId)
					.switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE)))
					.flatMap(function)
					.flatMap(t -> saveAuthorImage(t.getImageName(), path))
					.doFinally(t -> path.toFile().delete())
					.then();
		}
		
		return authorRepository.findById(id)
				.filter(t -> t.getCreatedBy() == userId)
				.switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE)))
				.flatMap(function)
				.then();
		
	}
	
	public Mono<Boolean> canUserModify(Integer id, Integer userId) {
		return authorRepository.findById(id)
				.switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)))
		.map(t -> t.getCreatedBy() == userId);
	}
	
	private Mono<Void> saveAuthorImage(UUID name, Path pathToFile) {
		if (name == null) return Mono.empty();

		MultipartBodyBuilder multipartbuilder = new MultipartBodyBuilder();
		
		multipartbuilder.part("file", new FileSystemResource(pathToFile));
		multipartbuilder.part("name", name.toString());
		
		return builder.build().post().uri("http://image-service/api/images/")
				.body(BodyInserters.fromMultipartData(multipartbuilder.build()))
				.retrieve()
				.bodyToMono(Void.class);
		
	}
	
	public Mono<Void> deleteAuthorImage(Integer id) {
		return authorRepository
			.findById(id)
			.doOnNext(t -> {
				kafkaImageProducer.sendMessageToDeleteImage(new ImageDeletionMessage(t.getImageName()));
				t.setImageName(null);
			})
			.flatMap(authorRepository::save)
			.then();
	}
}
