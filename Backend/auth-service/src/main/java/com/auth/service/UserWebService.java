package com.auth.service;

import com.auth.payload.request.AuthRequest;
import com.auth.payload.response.UserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserWebService {

	private final WebClient.Builder webClient;
	
	public Mono<UserDetails> getUserDetails(AuthRequest authRequest) {
		return webClient.build()
                .post()
                .uri("http://user-service/api/users/validate")
                .bodyValue(authRequest)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN)))
                .bodyToMono(UserDetails.class);
	}
	
}
