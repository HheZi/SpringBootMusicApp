package com.app.service;

import java.util.List;
import java.util.UUID;

import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import com.app.model.Author;
import com.app.payload.request.AuthorCreateOrUpdateRequest;
import com.app.payload.request.SaveAuthorImageRequest;
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
	
	public Mono<AuthorResponse> getAuthorById(Integer id) {
		return authorRepository
				.findById(id)
				.switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)))
				.map(authorMapper::fromAuthorToAuthorResponse);
	}
	
	public Flux<AuthorResponse> getAuthorsByIds(List<Integer> ids){
		return authorRepository.findAllById(ids)
				.map(authorMapper::fromAuthorToAuthorResponse);
	}
	
	public Flux<AuthorResponse> getAuthorByFirstSymbols(String symbols){
		return authorRepository
				.findByNameStartingWithIgnoreCase(symbols)
				.switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)))
				.map(authorMapper::fromAuthorToAuthorResponse);
	}

	public Mono<Integer> saveAuthor(AuthorCreateOrUpdateRequest dto, Integer userId) {
		Author author = authorMapper.fromAuthorRequestToAuthor(dto,  userId, dto.getFile() != null);
		
		return authorRepository
				.save(author)
				.doOnNext(t -> saveAuthorImage(t.getImageName(), dto.getFile()))
				.map(Author::getId);
	}
	
	public Mono<Void> updateAuthor(AuthorCreateOrUpdateRequest dto, Integer id, Integer userId){
		return authorRepository.findById(id)
		.filter(t -> t.getCreatedBy() == userId)
		.switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "You can't update the author")))
		.doOnNext(t -> {
			if (dto.getName() != null && !dto.getName().isEmpty() && !dto.getName().isBlank()) {
				t.setName(dto.getName());
			}
			if (t.getImageName() == null && dto.getFile() != null) {
				t.setImageName(UUID.randomUUID());
			}
			saveAuthorImage(t.getImageName(), dto.getFile());
		})
		.flatMap(authorRepository::save)
		.then();
		
	}
	
	public Mono<Boolean> canUserModify(Integer id, Integer userId) {
		return authorRepository.findById(id)
				.switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)))
		.map(t -> t.getCreatedBy() == userId);
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
	    			.bodyValue(new SaveAuthorImageRequest(name.toString(), t))
	    			.retrieve()
	    			.bodyToMono(Void.class);
	    }).subscribe();
		
	}
	
}
