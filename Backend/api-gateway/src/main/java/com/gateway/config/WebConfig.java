package com.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;

import com.gateway.config.filter.AuthenticationGatewayFilter;
import com.gateway.config.filter.TracksAggregationFilter;

@Configuration
public class WebConfig {

	@Autowired
	private AuthenticationGatewayFilter authenticationGatewayFilter;
	
	@Autowired
	private TracksAggregationFilter aggregationFilter;

	@Bean
	RouteLocator routeLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				.route("audio-service",
						t -> t.path("/api/audio/**").filters(f -> f.filter(authenticationGatewayFilter)).uri("lb://audio-service"))
				
				.route("auth-service",
						t -> t.path("/api/auth/**").filters(f -> f.filter(authenticationGatewayFilter)).uri("lb://auth-service"))
				
				.route("user-service", 
						t -> t.path("/api/users/**").filters(f -> f.filter(authenticationGatewayFilter)).uri("lb://user-service"))
				
				.route("track-service", 
						t -> t.path("/api/tracks/**").filters(f -> f.filter(authenticationGatewayFilter)).uri("lb://track-service"))
				
				.route("author-service", 
						t -> t.path("/api/authors/**").filters(f -> f.filter(authenticationGatewayFilter)).uri("lb://author-service"))
				
				.route("playlist-service", 
						t -> t.path("/api/playlists/**").filters(f -> f.filter(authenticationGatewayFilter)).uri("lb://playlist-service"))
				
				.route("image-service", 
						t -> t.path("/api/images/**").filters(f -> f.filter(authenticationGatewayFilter)).uri("lb://image-service"))

				.route(t -> t.path("/tracks").filters(f -> f.filters(authenticationGatewayFilter, aggregationFilter)).uri("http://localhost"))
				
				.build();
	}
	
	@Bean
	@LoadBalanced
	WebClient.Builder builder(){
		return WebClient.builder();
	}
	

}
