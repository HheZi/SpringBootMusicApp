package com.gateway.filter;

import com.gateway.model.UserJwtPayload;
import com.gateway.utils.EndpointUtils;
import com.gateway.utils.JwtUtil;
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
import reactor.core.publisher.Mono;

import java.util.Objects;

import static java.util.Objects.isNull;
import static org.springframework.util.StringUtils.hasText;

@RefreshScope
@Component
public class AuthenticationGatewayFilter implements GatewayFilter {

	@Autowired
	private JwtUtil jwtUtil;

	private final static String HEADER_USER_ID = "User-Id";

	private final static String HEADER_USER_ROLE = "User-Role";

	private final static String BEARER_PREFIX = "Bearer ";

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();

		String token = getTokenFromHeader(request);
		
		boolean isJwtTokenNotPresent = isNull(token);
		
		boolean isOpenClosedEndpoint = EndpointUtils.isOpenClosedEndpoints(request);
		boolean isOpenEndpoint = EndpointUtils.isOpenEndpoint(request);


		if (isOpenEndpoint || (isOpenClosedEndpoint && isJwtTokenNotPresent)) {
			return chain.filter(exchange);	
		}
		
		if (isJwtTokenNotPresent || isJwtExpired(token)) {
			return Mono.error(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
		}


		UserJwtPayload payload = jwtUtil.getUserJwtPayload(token);

		updateRequest(exchange, payload);

		return chain.filter(exchange);
	}

	private void updateRequest(
			ServerWebExchange exchange,
			UserJwtPayload payload
	) {
		exchange
				.getRequest()
				.mutate()
				.header(HEADER_USER_ID, payload.getUserId())
				.header(HEADER_USER_ROLE, payload.getUserRole())
				.build();
	}
	
	private boolean isJwtExpired(String token) {
		return jwtUtil.isExpired(token);
	}

	private String getTokenFromHeader(ServerHttpRequest request) {
		String auth = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

		if (hasText(auth) && auth.startsWith(BEARER_PREFIX))
			return auth.substring(BEARER_PREFIX.length());

		return null;
	}

}
