package com.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import reactor.core.publisher.Mono;

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
	
}
