package com.auth.util;

import java.time.Instant;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.relational.core.sql.From;
import org.springframework.stereotype.Component;

import com.auth.payload.response.UserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
public class JwtUtil {

	@Value("${token.secret}")
	private String SECRET_KEY;
	
	private final Long EXPIRATION_TIME_IN_MINUTES = 60L;
	
	public String createJwtToken(Integer userId) {
		return Jwts.builder()
				.claim("id", userId)
				.issuedAt(new Date())
				.expiration(Date.from(Instant.now().plusMillis(TimeUnit.MINUTES.toMillis(EXPIRATION_TIME_IN_MINUTES))))
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
