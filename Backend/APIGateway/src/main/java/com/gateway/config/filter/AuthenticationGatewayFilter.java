package com.gateway.config.filter;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import com.gateway.utils.JwtUtil;

import reactor.core.publisher.Mono;

@RefreshScope
@Component
public class AuthenticationGatewayFilter implements GatewayFilter {

	public final List<String> openEndpoints = List.of("/login", "/api/users/");

	@Autowired
	private JwtUtil jwtUtil;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();

		if (isEndpointSecured(request) && isJwtExpired(request)) {
			return Mono.error(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
		}
		
		return chain.filter(exchange);
	}

	private boolean isJwtExpired(ServerHttpRequest request) {
		List<String> vals = request.getHeaders().getOrEmpty("Authorization");
		return  vals.isEmpty() || jwtUtil.isExpired(vals.get(0));
	}

	private boolean isEndpointSecured(ServerHttpRequest request) {
		return openEndpoints.stream().noneMatch(t -> request.getURI().getPath().contains(t));
	}

}
