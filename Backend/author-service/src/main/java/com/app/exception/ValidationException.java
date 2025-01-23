package com.app.exception;

import lombok.Getter;

@Getter
public class ValidationException extends Throwable{

	private static final long serialVersionUID = -5431192981872254120L;

	private String reason;

	public ValidationException(String reason) {
		super(reason);
		this.reason = reason;
	}
	
}
