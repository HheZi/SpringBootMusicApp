package com.gateway.config.filter;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
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
	
	
	private final List<String> openEndpoints = List.of("/login", "/api/users/", "/api/audio/");

	@Autowired
	private JwtUtil jwtUtil;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();

		String token = getTokenFromHeader(request);
		
		if (isEndpointNotSecured(request) && isJwtExpired(token)) {
			return Mono.error(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
		}
		
		HttpHeaders headers = exchange.getRequest().getHeaders();
		
		
//		headers.add("userId", jwtUtil.getValue("id", token));
//		headers.add("username", jwtUtil.getValue("username", token));
		
		return chain.filter(exchange);
	}

	private boolean isJwtExpired(String token) {
		return  token == null || jwtUtil.isExpired(token);
	}
	
	private String getTokenFromHeader(ServerHttpRequest request) {
		List<String> vals = request.getHeaders().get(HttpHeaders.AUTHORIZATION);
		return vals == null ? null : vals.get(0).split("\s")[1];
	}

	private boolean isEndpointNotSecured(ServerHttpRequest request) {
		return openEndpoints.stream().noneMatch(t -> request.getURI().getPath().contains(t));
	}

}
