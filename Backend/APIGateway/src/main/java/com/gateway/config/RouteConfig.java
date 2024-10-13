package com.gateway.config;


import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

@Configuration
public class RouteConfig {
	@Bean
	RouteLocator routeLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				.route("audio-service-route", t -> t.path("/api/audio/**").uri("lb://AudioService"))
				.route("auth-service-route", t -> t.path("/login").uri("lb://AuthService"))
				.route("user-service-route", t -> t.path("/api/users/**").and().method(HttpMethod.values()).uri("lb://UserService"))
				.build();
	}
	
}
