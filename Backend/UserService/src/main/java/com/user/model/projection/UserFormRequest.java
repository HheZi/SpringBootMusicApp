package com.user.model.projection;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserFormRequest {

	private String username;
	
	private String email;
	
	private String password;
	
}
