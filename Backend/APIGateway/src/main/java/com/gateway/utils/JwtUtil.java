package com.gateway.utils;

import java.util.Date;

import javax.crypto.SecretKey;

import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

	@Value("${token.secret}")
	private String SECRET_KEY;

	private Claims getClaims(String token) {
		return (Claims) Jwts.parser().verifyWith(getSingingKey()).build().parseSignedClaims(token).getPayload();
	}

	public boolean isExpired(String token) {
		return getClaims(token).getExpiration().before(new Date());
	}

	private SecretKey getSingingKey() {
		return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
	}

}
