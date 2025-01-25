package com.gateway.config.filter;

import org.springframework.http.HttpMethod;


public record Endpoint(String uri, HttpMethod[] httpMethods) {}
