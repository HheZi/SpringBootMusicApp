package com.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import com.gateway.config.filter.AuthenticationGatewayFilter;

@Configuration
public class RouteConfig {

	@Autowired
	private AuthenticationGatewayFilter filter;

	@Bean
	RouteLocator routeLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				.route("audio-service",
						t -> t.path("/api/audio/**").filters(f -> f.filter(filter)).uri("lb://audio-service"))
				
				.route("auth-service",
						t -> t.path("/login").filters(f -> f.filter(filter)).uri("lb://auth-service"))
				
				.route("user-service", 
						t -> t.path("/api/users/**").filters(f -> f.filter(filter)).uri("lb://user-service"))
				
				.route("track-service", 
						t -> t.path("/api/tracks/**").filters(f -> f.filter(filter)).uri("lb://track-service"))
				
				.route("author-service", 
						t -> t.path("/api/authors/**").filters(f -> f.filter(filter)).uri("lb://author-service"))
				
				.route("playlist-service", 
						t -> t.path("/api/playlists/**").filters(f -> f.filter(filter)).uri("lb://playlist-service"))
				.build();
	}

}
