package com.app.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.model.projection.AuthorResponse;
import com.app.service.AuthorService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/authors/")
@RequiredArgsConstructor
public class AuthorController {

	private final AuthorService authorService;
	
	@GetMapping
	public Flux<AuthorResponse> getAuthors(@RequestParam("ids") List<Integer> ids){
		return authorService.getAuthorById(ids);
	}
	
	@GetMapping("/{symbol}")
	public Flux<AuthorResponse> getAuthorsBySymbols(@PathVariable("symbol") String symbol){
		return authorService.getAuthorByFirstSymbols(symbol);
	}
	
	@PostMapping
	public Mono<ResponseEntity<?>> createAuthor(@RequestBody Mono<String> name) {
		return name.doOnNext(authorService::saveAuthor)
				.flatMap(t -> Mono.just(ResponseEntity.status(HttpStatus.CREATED).build()));
	}
	
	
}
