package com.user.model.projection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class UserAuthRequest {

	private String username;

	private String password;

}
