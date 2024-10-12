package com.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {
	
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		return httpSecurity
				.logout(t -> t.disable())
				.formLogin(t -> t.disable())
				.csrf(t -> t.disable())
				.authorizeHttpRequests(t -> t.anyRequest().permitAll())
				.build();
		
	}

	@Bean
	PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}
}
