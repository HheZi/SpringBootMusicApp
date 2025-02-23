package com.app.exception;

import lombok.Getter;

import java.io.Serial;

@Getter
public class FileValidationException extends RuntimeException{

	@Serial
	private static final long serialVersionUID = -5431192981872254120L;

	private final String reason;

	public FileValidationException(String reason) {
		super(reason);
		this.reason = reason;
	}
	
}
