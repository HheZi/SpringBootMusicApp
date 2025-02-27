package com.app.exception.advice;

import com.app.exception.AuthorNameException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.List;

@RestControllerAdvice
public class AuthorNameExceptionAdviceController {

    @ExceptionHandler(AuthorNameException.class)
    public ResponseEntity<List<String>> exception(AuthorNameException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Collections.singletonList(e.getReason()));
    }

}
