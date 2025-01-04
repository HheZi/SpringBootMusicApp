package com.auth.util;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

	@Value("${token.secret}")
	private String SECRET_KEY;
	
	@Value("${jwt.expirationInMinutes}")
	private Long EXPIRATION_TIME_IN_MINUTES;
	
	public String createJwtToken(Integer userId) {
		return Jwts.builder()
				.claim("id", userId)
				.issuedAt(new Date())
				.expiration(Date.from(Instant.now().plus(EXPIRATION_TIME_IN_MINUTES, ChronoUnit.MINUTES)))
				.signWith(getSingingKey())
				.compact();
	
	}
	
	
	public Claims getClaims(String token) {
		return (Claims) Jwts.parser().decryptWith(getSingingKey()).build().parse(token).getPayload();
	}
	
	public boolean isExpired(String token) {
		return getClaims(token).getExpiration().before(new Date());
	}
	
	private SecretKey getSingingKey() {
		return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
	}
}
