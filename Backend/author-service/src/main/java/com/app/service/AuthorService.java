package com.app.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.app.model.Author;
import com.app.model.projection.AuthorResponse;
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
	
	public Flux<AuthorResponse> getAuthorById(List<Integer> id) {
		return authorRepository
				.findAllById(id)
				.map(authorMapper::fromAuthorToAuthorResponse);
	}
	
	public Flux<AuthorResponse> getAuthorByFirstSymbols(String symbols){
		return authorRepository
				.findByNameStartingWithIgnoreCase(symbols)
				.map(authorMapper::fromAuthorToAuthorResponse);
	}

	public void saveAuthor(String nameOfAuthor) {
		Author author = new Author(null, nameOfAuthor);
		
		authorRepository.save(author);
	}
	
}
