package com.user.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BadValidationResponse {

	private static final long serialVersionUID = 1519413422684127113L;

	private String field;

	private String message;

}
