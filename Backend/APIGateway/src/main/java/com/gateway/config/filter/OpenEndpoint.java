package com.gateway.config.filter;

import org.springframework.http.HttpMethod;


public record OpenEndpoint(String uri, HttpMethod[] httpMethods) {
	
}
