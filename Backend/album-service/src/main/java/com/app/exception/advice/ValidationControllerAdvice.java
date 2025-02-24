package com.app.exception.advice;

import com.app.exception.FileValidationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.Collections;
import java.util.List;

@RestControllerAdvice
public class ValidationControllerAdvice {

	@ExceptionHandler(WebExchangeBindException.class)
	public ResponseEntity<?> bindError(WebExchangeBindException bindException){
		return ResponseEntity.badRequest()
				.body(bindException.getBindingResult()
						.getFieldErrors()
						.stream()
						.map(DefaultMessageSourceResolvable::getDefaultMessage)
				);
	}

	@ExceptionHandler(FileValidationException.class)
	public ResponseEntity<List<String>> exception(FileValidationException e){
		return ResponseEntity.badRequest().body(Collections.singletonList(e.getReason()));
	}
	
}
