package com.auth.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class AuthResponse {

	private String token;
	
	private String refreshToken;
	
}
