package com.app.controller;


import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.payload.request.AuthorCreateRequest;
import com.app.payload.response.AuthorResponse;
import com.app.service.AuthorService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/authors/")
@RequiredArgsConstructor
public class AuthorController {

	private final AuthorService authorService;
	
	@GetMapping("/{id}")
	public Mono<AuthorResponse> getAuthor(@PathVariable("id") Integer id){
		return authorService.getAuthorById(id);
	}
	
	@GetMapping
	public Flux<AuthorResponse> getAuthorsByIds(@RequestParam("id[]") List<Integer> ids){
		return authorService.getAuthorsByIds(ids);
	}
	
	@GetMapping("/symbol/{symbol}")
	public Flux<AuthorResponse> getAuthorsBySymbols(@PathVariable("symbol") String symbol){
		return authorService.getAuthorByFirstSymbols(symbol);
	}
	
	@PostMapping
	public Mono<ResponseEntity<?>> createAuthor(@RequestBody Mono<AuthorCreateRequest> dto) {
		return dto.map(authorService::saveAuthor)
				.flatMap(t -> Mono.just(ResponseEntity.status(HttpStatus.CREATED).body(t)));
	}
	
	
}
