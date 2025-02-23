package com.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

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
		return (Claims) Jwts.parser().verifyWith(getSingingKey()).build().parseSignedClaims(token).getPayload();
	}
	
	public boolean isExpired(String token) {
		try {
			return getClaims(token).getExpiration().before(new Date());
		} catch (ExpiredJwtException e) {
			return true;
		}
	}
	
	private SecretKey getSingingKey() {
		return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
	}
}
