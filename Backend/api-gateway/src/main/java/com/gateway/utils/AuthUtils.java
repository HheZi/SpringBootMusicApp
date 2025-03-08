package com.gateway.utils;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.util.Optional;

import static org.springframework.util.StringUtils.hasText;

@UtilityClass
public class AuthUtils {

    private final static String BEARER_PREFIX = "Bearer ";

    public static Optional<String> getTokenFromHeader(ServerHttpRequest request) {
        String auth = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (hasText(auth) && auth.startsWith(BEARER_PREFIX))
            return Optional.of(auth.substring(BEARER_PREFIX.length()));

        return Optional.empty();
    }

}
