package com.user.exceptions.advice;

import com.user.exceptions.UserRuntimeException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.Collections;
import java.util.List;

@RestControllerAdvice
public class UserControllerAdvice {

	@ExceptionHandler(WebExchangeBindException.class)
	public ResponseEntity<List<String>> validationError(WebExchangeBindException e) {
		return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
				.body(e.getBindingResult().getAllErrors().stream()
						.map(DefaultMessageSourceResolvable::getDefaultMessage)
						.toList());
	}

	@ExceptionHandler(UserRuntimeException.class)
	public ResponseEntity<List<String>> userRuntimeException(UserRuntimeException e){
		return ResponseEntity.badRequest().body(Collections.singletonList(e.getReason()));
	}

}
