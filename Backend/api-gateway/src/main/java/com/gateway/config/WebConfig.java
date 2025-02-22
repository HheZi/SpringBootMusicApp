package com.gateway.config;

import com.gateway.config.filter.AuthenticationGatewayFilter;
import com.gateway.config.filter.aggregation.AlbumAggregationFilter;
import com.gateway.config.filter.aggregation.TracksAggregationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebConfig {

	@Autowired
	private AuthenticationGatewayFilter authenticationGatewayFilter;
	
	@Autowired
	private TracksAggregationFilter trackAggregationFilter;
	
	@Autowired
	private AlbumAggregationFilter albumAggregationFilter;

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
				
				.route("album-service", 
						t -> t.path("/api/albums/**").filters(f -> f.filter(authenticationGatewayFilter)).uri("lb://album-service"))
				
				.route("image-service", 
						t -> t.path("/api/images/**").filters(f -> f.filter(authenticationGatewayFilter)).uri("lb://image-service"))
				
				.route("favorite-service", 
						t -> t.path("/api/favorites/**").filters(f -> f.filter(authenticationGatewayFilter)).uri("lb://favorite-service"))

				.route("playlist-service", 
						t -> t.path("/api/playlists/**").filters(f -> f.filter(authenticationGatewayFilter)).uri("lb://playlist-service"))
				
				.route(t -> t.path("/tracks").filters(f -> f.filters(authenticationGatewayFilter, trackAggregationFilter)).uri("http://localhost"))
				
				.route(t -> t.path("/albums/*").filters(f -> f.filters(authenticationGatewayFilter, albumAggregationFilter)).uri("http://localhost"))
				
				.build();
	}
	
	@Bean
	@LoadBalanced
	WebClient.Builder builder(){
		return WebClient.builder();
	}
	

}
