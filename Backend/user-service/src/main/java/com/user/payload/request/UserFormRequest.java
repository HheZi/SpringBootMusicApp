package com.user.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class UserFormRequest {

	@NotBlank(message = "The username can't be blank")
	private String username;

	@Email(message = "The email must follow the email template")
	@NotBlank(message = "The email can't be blank")
	private String email;

	@NotBlank(message = "The password can't be blank")
	private String password;

}
