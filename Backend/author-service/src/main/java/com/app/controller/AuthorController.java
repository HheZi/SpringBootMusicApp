package com.app.controller;


import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.payload.request.AuthorCreateOrUpdateRequest;
import com.app.payload.response.AuthorResponse;
import com.app.service.AuthorService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.web.bind.annotation.PutMapping;

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
	public Flux<AuthorResponse> getAuthorsByIds(@RequestParam("ids") List<Integer> ids){
		return authorService.getAuthorsByIds(ids);
	}
	
	@GetMapping("/symbol/{symbol}")
	public Flux<AuthorResponse> getAuthorsBySymbols(@PathVariable("symbol") String symbol){
		return authorService.getAuthorByFirstSymbols(URLDecoder.decode(symbol, Charset.defaultCharset()));
	}
	
	@GetMapping("owner/{id}")
	public Mono<Boolean> canModify(
			@PathVariable("id")Integer id, 
			@RequestHeader("userId") Integer userId
		){
		return authorService.canUserModify(id, userId);
	}
	
	@PostMapping
	public Mono<ResponseEntity<?>> createAuthor(
			@Valid @ModelAttribute AuthorCreateOrUpdateRequest dto, 
			@RequestHeader("userId") Integer userId
		) {
		return authorService.saveAuthor(dto, userId)
				.flatMap(t -> Mono.just(ResponseEntity.status(HttpStatus.CREATED).body(t)));
	}
	
	@PutMapping("{id}")
	public Mono<Void> updateAuthor(
			@Valid @ModelAttribute AuthorCreateOrUpdateRequest body, 
			@PathVariable("id") Integer id,
			@RequestHeader("userId") Integer userId
		) {
		return authorService.updateAuthor(body, id, userId);
	}
	
}
