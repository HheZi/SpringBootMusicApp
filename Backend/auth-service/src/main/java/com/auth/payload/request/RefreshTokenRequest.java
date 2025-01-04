package com.auth.payload.request;

import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RefreshTokenRequest {

	private UUID refreshToken;
	
}
