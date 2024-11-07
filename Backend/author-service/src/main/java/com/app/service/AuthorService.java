package com.app.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.app.model.Author;
import com.app.payload.request.AuthorCreateRequest;
import com.app.payload.response.AuthorResponse;
import com.app.repository.AuthorRepository;
import com.app.util.AuthorMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthorService {

	private final AuthorRepository authorRepository;

	private final AuthorMapper authorMapper;
	
	public Mono<AuthorResponse> getAuthorById(Integer id) {
		return authorRepository
				.findById(id)
				.switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The author is not found")))
				.map(authorMapper::fromAuthorToAuthorResponse);
	}
	
	public Flux<AuthorResponse> getAuthorsByIds(List<Integer> ids){
		return authorRepository.findAllById(ids)
				.map(authorMapper::fromAuthorToAuthorResponse);
	}
	
	public Flux<AuthorResponse> getAuthorByFirstSymbols(String symbols){
		return authorRepository
				.findByNameStartingWithIgnoreCase(symbols)
				.map(authorMapper::fromAuthorToAuthorResponse);
	}

	public Mono<Integer> saveAuthor(AuthorCreateRequest dto) {
		Author author = new Author(null, dto.getName());
		
		return authorRepository.save(author).map(Author::getId);
	}
	
}
