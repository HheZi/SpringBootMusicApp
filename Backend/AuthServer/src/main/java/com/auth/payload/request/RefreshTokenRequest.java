package com.auth.payload.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RefreshTokenRequest {

	private String refreshToken;
	
}
