package com.gateway.config.filter;

import static org.springframework.http.HttpMethod.*;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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

	private final List<OpenEndpoint> openEndpoints = List.of(
			new OpenEndpoint("/api/auth", new HttpMethod[] { POST }),
			new OpenEndpoint("/api/users", new HttpMethod[] { POST }),
			new OpenEndpoint("/api/audio", new HttpMethod[] { GET }),
			new OpenEndpoint("/api/images", new HttpMethod[] { GET }),
			new OpenEndpoint("/api/tracks", new HttpMethod[] { GET }),
			new OpenEndpoint("/api/playlists", new HttpMethod[] { GET }),
			new OpenEndpoint("/tracks", new HttpMethod[] { GET })
		);

	@Autowired
	private JwtUtil jwtUtil;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();

		String token = getTokenFromHeader(request);

		if (isEndpointNotSecured(request)) {
			return chain.filter(exchange);
		}
		
		if (isJwtExpired(token)) {
			return Mono.error(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
		}

		exchange.getRequest()
		.mutate()
		.header("userId", jwtUtil.getValue("id", token))
		.build();

		return chain.filter(exchange);
	}

	private boolean isJwtExpired(String token) {
		return token == null || jwtUtil.isExpired(token);
	}

	private String getTokenFromHeader(ServerHttpRequest request) {
		List<String> vals = request.getHeaders().get(HttpHeaders.AUTHORIZATION);
		return vals == null ? null : vals.get(0).split("\s+")[1];
	}

	private boolean isEndpointNotSecured(ServerHttpRequest request) {
	    return openEndpoints.stream()
	            .anyMatch(endpoint -> isPathTheSame(request, endpoint.uri()) && isHttpMethodTheSame(request, endpoint.httpMethods()));
	}

	private boolean isPathTheSame(ServerHttpRequest request, String uri) {
		return request.getURI().getPath().contains(uri);
	}

	private boolean isHttpMethodTheSame(ServerHttpRequest request, HttpMethod[] httpMethods) {
		return Stream.of(httpMethods).anyMatch(method -> method.equals(request.getMethod()));
	}

}
