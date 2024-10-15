package com.user.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import com.user.model.dto.BadValidationResponse;

@RestControllerAdvice
public class ValidationControllerAdvice {
	
	@ExceptionHandler(WebExchangeBindException.class)
	public ResponseEntity<List<String>> validationError(WebExchangeBindException e) {
		return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
				.body(e.getBindingResult()
						.getAllErrors()
						.stream()
						.map(t -> t.getDefaultMessage())
						.toList());
	}
	
}
