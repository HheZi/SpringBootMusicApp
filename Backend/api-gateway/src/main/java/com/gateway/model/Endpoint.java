package com.gateway.model;

import org.springframework.http.HttpMethod;


public record Endpoint(String uri, HttpMethod[] httpMethods) {}
