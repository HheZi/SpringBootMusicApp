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
				.route("audio-service-route",
						t -> t.path("/api/audio/**").filters(f -> f.filter(filter)).uri("lb://AudioService"))
				
				.route("auth-service-route",
						t -> t.path("/login").filters(f -> f.filter(filter)).uri("lb://AuthService"))
				
				.route("user-service-route", t -> t.path("/api/users/**").and().method(HttpMethod.values())
						.filters(f -> f.filter(filter)).uri("lb://UserService"))
				.build();
	}

}
