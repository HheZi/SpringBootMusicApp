package com.app.exception.advice;

import com.app.exception.FileValidationException;
import com.app.exception.model.BadFileValidation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class FileValidationExceptionControllerAdvice {

    @ExceptionHandler(FileValidationException.class)
    public ResponseEntity<BadFileValidation> exception(FileValidationException e){

        BadFileValidation badFileValidation = new BadFileValidation(e.getReason());

        return ResponseEntity.badRequest().body(badFileValidation);
    }

}
