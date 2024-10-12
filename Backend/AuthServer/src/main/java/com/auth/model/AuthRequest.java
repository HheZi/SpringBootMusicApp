package com.auth.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Value;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AuthRequest {
	
	private String username;
	
	private String password;
	
}
