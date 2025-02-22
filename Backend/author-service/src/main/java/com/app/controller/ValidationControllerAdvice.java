package com.app.controller;

import com.app.exception.ValidationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.util.List;

@RestControllerAdvice
public class ValidationControllerAdvice {

	@ExceptionHandler(WebExchangeBindException.class)
	public Mono<ResponseEntity<?>> bindError(WebExchangeBindException bindException){
		return Mono.just(ResponseEntity.badRequest()
				.body(bindException.getBindingResult()
						.getFieldErrors()
						.stream()
						.map(t -> t.getDefaultMessage())
				));
	}
	
	@ExceptionHandler(ValidationException.class)
	public Mono<ResponseEntity<?>> bindError(ValidationException exception){
		return Mono.just(ResponseEntity.badRequest().body(List.of(exception.getReason())));
	}
	
}
