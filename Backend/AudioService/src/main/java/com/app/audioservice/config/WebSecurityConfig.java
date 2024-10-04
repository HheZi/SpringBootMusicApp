package com.app.audioservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	@Bean
	SecurityFilterChain chain(HttpSecurity http) throws Exception {
		return http
				.formLogin(t -> t.disable())
				.logout(t -> t.disable())
				.authorizeHttpRequests(t -> t.anyRequest().permitAll())
				.build();
	}
	
}
