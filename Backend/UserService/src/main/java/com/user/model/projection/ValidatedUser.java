package com.user.model.projection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ValidatedUser {
	
	private Integer id;
	
	private String username;
	
}
