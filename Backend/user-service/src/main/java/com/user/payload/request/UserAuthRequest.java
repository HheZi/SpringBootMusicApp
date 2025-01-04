package com.user.payload.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserAuthRequest {

	private String username;

	private String password;

}
