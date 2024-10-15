package com.user.model.projection;

import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class UserFormRequest {

	@NotBlank(message = "The username can't be blank")
	@Length(min = 4, message = "The username lenght must be at least 4 characters long")
	private String username;
	
	@Email(message = "The email must follow the email template")
	@NotBlank(message =  "The email can't be blank")
	private String email;
	
	@NotBlank(message = "The password can't be blank")
	private String password;
	
}
